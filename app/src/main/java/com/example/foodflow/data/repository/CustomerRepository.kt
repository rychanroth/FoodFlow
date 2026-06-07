package com.example.foodflow.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.example.foodflow.data.model.AppUser
import com.example.foodflow.data.model.CartItem
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.data.model.Order
import com.example.foodflow.data.model.OrderItem
import com.example.foodflow.data.model.OrderStatus
import com.example.foodflow.data.model.PaymentMethod
import com.example.foodflow.data.model.PlatformSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.tasks.await
import kotlin.collections.map

class CustomerRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getUserById(uid: String): Result<AppUser> {
        return try {
            val document = firestore.collection("users").document(uid).get().await()
            val user = document.toObject(AppUser::class.java)
            if (user != null) Result.success(user) else Result.failure(Exception("User not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getRestaurants(): Flow<List<AppUser>> = callbackFlow {
        val subscription = firestore.collection("users")
            .whereEqualTo("role", "RESTAURANT")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val restaurants = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(AppUser::class.java)
                } ?: emptyList()
                trySend(restaurants)
            }
        awaitClose { subscription.remove() }
    }

    fun getNewlyAddedItems(): Flow<List<MenuItem>> = callbackFlow {
        val subscription = firestore.collection("menu_items")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(10)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(MenuItem::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { subscription.remove() }
    }

    fun getMenuForRestaurant(restaurantId: String): Flow<List<MenuItem>> = callbackFlow {
        val subscription = firestore.collection("menu_items")
            .whereEqualTo("restaurantId", restaurantId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(MenuItem::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun placeOrder(order: Order): Result<String> {
        return try {
            val documentRef = firestore.collection("orders").document()
            val newOrder = order.copy(id = documentRef.id)
            documentRef.set(newOrder).await()
            Result.success(documentRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Listen for orders placed by a specific Customer
    fun getOrdersForCustomer(customerId: String): Flow<List<Order>> = callbackFlow {
        val subscription = firestore.collection("orders")
            .whereEqualTo("customerId", customerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val orders = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Order::class.java)?.copy(id = doc.id)
                }?.sortedByDescending { it.createdAt } ?: emptyList() // Newest first

                trySend(orders)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun updateOrderTransactionProof(orderId: String, imageUrl: String) {
        val updates = hashMapOf<String, Any>(
            "transactionImageUrl" to imageUrl
        )
        firestore.collection("orders").document(orderId).update(updates).await()
    }

    suspend fun placeOrder(
        customerId: String,
        restaurantId: String,
        cartItems: List<CartItem>,
        paymentMethod: PaymentMethod,
        platformSettings: PlatformSettings
    ): Result<String> {
        return try {
            // 1. Fetch Documents for Denormalization
            val customerDoc = firestore.collection("users").document(customerId).get().await()
            val customer = customerDoc.toObject(AppUser::class.java)

            val restaurantDoc = firestore.collection("users").document(restaurantId).get().await()
            val restaurant = restaurantDoc.toObject(AppUser::class.java)

            // 2. Map CartItems to OrderItems (Point-in-time snapshot)
            val orderItems = cartItems.map { cartItem ->
                OrderItem(
                    menuItemId = cartItem.menuItem.id,
                    name = cartItem.menuItem.name,
                    quantity = cartItem.quantity,
                    price = cartItem.menuItem.price,
                    imageUrl = cartItem.menuItem.imageUrl
                )
            }

            // 3. Calculate the Economics
            val subtotal = cartItems.sumOf { it.menuItem.price * it.quantity }
            val deliveryFee = platformSettings.deliveryFee
            val platformFee = platformSettings.platformFlatFee
            val totalAmount = subtotal + deliveryFee + platformFee

            val restaurantEarnings = subtotal - (subtotal * platformSettings.platformCommissionRate)
            val driverEarnings = deliveryFee * platformSettings.driverCommissionRate
            val platformEarnings = (subtotal * platformSettings.platformCommissionRate) + (deliveryFee * (1 - platformSettings.driverCommissionRate)) + platformFee

            // 4. Determine Initial Status
            val initialStatus = if (paymentMethod == PaymentMethod.BANK_TRANSFER) {
                OrderStatus.PENDING_PAYMENT_VERIFICATION
            } else {
                OrderStatus.PLACED
            }

            // 5. Construct the Order
            val documentRef = firestore.collection("orders").document()
            val newOrder = Order(
                id = documentRef.id,
                customerId = customerId,
                restaurantId = restaurantId,
                items = orderItems,
                customerName = customer?.name ?: "Unknown Customer",
                restaurantName = restaurant?.name ?: "Unknown Restaurant",
                deliveryAddress = customer?.addresses?.find { it.isDefault }?.street ?: "No address provided",
                status = initialStatus,
                paymentMethod = paymentMethod,
                subtotal = subtotal,
                deliveryFee = deliveryFee,
                platformFee = platformFee,
                totalAmount = totalAmount,
                restaurantEarnings = restaurantEarnings,
                driverEarnings = driverEarnings,
                platformEarnings = platformEarnings
            )

            // 6. Write to Firestore
            documentRef.set(newOrder).await()
            Result.success(documentRef.id)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
