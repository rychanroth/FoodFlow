package com.example.foodflow.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.example.foodflow.data.model.Order
import com.example.foodflow.data.model.OrderStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class OrderRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val ordersCollection = firestore.collection("orders")

    // Listen for orders belonging to THIS restaurant, ordered by time
    fun getOrdersForThisRestaurant(restaurantId: String): Flow<List<Order>> = callbackFlow {
        val subscription = ordersCollection
            .whereEqualTo("restaurantId", restaurantId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val orders = snapshot?.documents?.mapNotNull { doc ->
                    val order = doc.toObject(Order::class.java)?.copy(id = doc.id)
                    order
                }?.sortedByDescending { it.createdAt } ?: emptyList()
                trySend(orders)
            }
        awaitClose { subscription.remove() }
    }

    // V2: Fetch today's orders for a restaurant for Dashboard aggregation
    fun getThisRestaurantOrdersForToday(restaurantId: String): Flow<List<Order>> = callbackFlow {
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val subscription = ordersCollection
            .whereEqualTo("restaurantId", restaurantId)
            .whereGreaterThanOrEqualTo("createdAt", startOfDay)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val orders = snapshot?.documents?.mapNotNull { it.toObject(Order::class.java)?.copy(id = it.id) }?.sortedByDescending { it.createdAt } ?: emptyList()
                trySend(orders)
            }
        awaitClose { subscription.remove() }
    }

    // Update order status
    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        val updates = hashMapOf<String, Any>(
            "status" to newStatus.name
        )
        ordersCollection.document(orderId).update(updates).await()
    }

    // Get orders that are READY and have NO driver assigned yet
    fun getAvailableOrders(): Flow<List<Order>> = callbackFlow {
        val subscription = ordersCollection
            .whereEqualTo("status", OrderStatus.READY.name)
            .whereEqualTo("driverId", null)
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
            .whereIn("status", listOf(OrderStatus.ON_THE_WAY.name, OrderStatus.READY.name))
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val orders = snapshot?.documents?.mapNotNull { it.toObject(Order::class.java)?.copy(id = it.id) } ?: emptyList()
                trySend(orders)
            }
        awaitClose { subscription.remove() }
    }

    // V2: Fetch driver's completed deliveries for Earnings tab (date range)
    fun getThisDriverEarnings(driverId: String, startDate: Long, endDate: Long): Flow<List<Order>> = callbackFlow {
        val subscription = ordersCollection
            .whereEqualTo("driverId", driverId)
            .whereEqualTo("status", OrderStatus.DELIVERED.name)
            .whereGreaterThanOrEqualTo("createdAt", startDate)
            .whereLessThanOrEqualTo("createdAt", endDate)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val orders = snapshot?.documents?.mapNotNull { it.toObject(Order::class.java)?.copy(id = it.id) }?.sortedByDescending { it.createdAt } ?: emptyList()
                trySend(orders)
            }
        awaitClose { subscription.remove() }
    }

    // V2: Fetch all platform orders for Admin Analytics (date range)
    fun getPlatformOrders(startDate: Long, endDate: Long): Flow<List<Order>> = callbackFlow {
        val subscription = ordersCollection
            .whereGreaterThanOrEqualTo("createdAt", startDate)
            .whereLessThanOrEqualTo("createdAt", endDate)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val orders = snapshot?.documents?.mapNotNull { it.toObject(Order::class.java)?.copy(id = it.id) }?.sortedByDescending { it.createdAt } ?: emptyList()
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