package com.example.foodflow.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.example.foodflow.data.model.Order
import com.example.foodflow.data.model.OrderStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class OrderRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val ordersCollection = firestore.collection("orders")

    // Listen for orders belonging to THIS restaurant, ordered by time
    fun getOrdersForRestaurant(restaurantId: String): Flow<List<Order>> = callbackFlow {
        val subscription = ordersCollection
            .whereEqualTo("restaurantId", restaurantId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val orders = snapshot?.documents?.mapNotNull { doc ->
                    // Firestore doesn't automatically map our custom CartItem list perfectly,
                    // so we map it manually to be safe.
                    val order = doc.toObject(Order::class.java)?.copy(id = doc.id)
                    order
                }?.sortedByDescending { it.createdAt } ?: emptyList() // Newest first

                trySend(orders)
            }
        awaitClose { subscription.remove() }
    }

    // Update order status
    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        val updates = hashMapOf<String, Any>(
            "status" to newStatus // Firestore will automatically save the enum name as a string
        )
        ordersCollection.document(orderId).update(updates).await()
    }

    // Get orders that are READY and have NO driver assigned yet
    fun getAvailableOrders(): Flow<List<Order>> = callbackFlow {
        val subscription = ordersCollection
            .whereEqualTo("status", OrderStatus.READY.name)
            .whereEqualTo("driverId", null) // Prevents race conditions!
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val orders = snapshot?.documents?.mapNotNull { it.toObject(Order::class.java)?.copy(id = it.id) } ?: emptyList()
                trySend(orders)
            }
        awaitClose { subscription.remove() }
    }

    // Get orders the specific driver is currently delivering
    fun getMyActiveDeliveries(driverId: String): Flow<List<Order>> = callbackFlow {
        val subscription = ordersCollection
            .whereEqualTo("driverId", driverId)
            .whereIn("status", listOf(OrderStatus.ON_THE_WAY.name, OrderStatus.READY.name)) // Just in case it didn't update yet
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val orders = snapshot?.documents?.mapNotNull { it.toObject(Order::class.java)?.copy(id = it.id) } ?: emptyList()
                trySend(orders)
            }
        awaitClose { subscription.remove() }
    }

    // Claim an order for the driver
    suspend fun acceptOrder(orderId: String, driverId: String) {
        val updates = hashMapOf<String, Any>(
            "driverId" to driverId,
            "status" to OrderStatus.ON_THE_WAY.name
        )
        ordersCollection.document(orderId).update(updates).await()
    }

    // Mark order as delivered
    suspend fun markAsDelivered(orderId: String) {
        val updates = hashMapOf<String, Any>(
            "status" to OrderStatus.DELIVERED.name
        )
        ordersCollection.document(orderId).update(updates).await()
    }
}