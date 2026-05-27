package com.example.foodflow.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.example.foodflow.data.model.Order
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

    // Update the status of an order
    suspend fun updateOrderStatus(orderId: String, newStatus: String) {
        val updates = hashMapOf<String, Any>(
            "status" to newStatus
        )
        ordersCollection.document(orderId).update(updates).await()
    }
}