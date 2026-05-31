package com.example.foodflow.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.example.foodflow.data.model.AppUser
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.data.model.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.tasks.await

class CustomerRepository {

    private val firestore = FirebaseFirestore.getInstance()

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
}
