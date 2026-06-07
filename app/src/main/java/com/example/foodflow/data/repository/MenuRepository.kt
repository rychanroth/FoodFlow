package com.example.foodflow.data.repository

import android.content.Context
import android.net.Uri
import com.example.foodflow.data.model.MenuItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MenuRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val menuCollection = firestore.collection("menu_items")

    // 1. READ: Get real-time menu items for a specific restaurant
    fun getMenuItems(restaurantId: String): Flow<List<MenuItem>> = callbackFlow {
        // Query Firestore
        val subscription = menuCollection
            .whereEqualTo("restaurantId", restaurantId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // Close the flow if there's an error
                    return@addSnapshotListener
                }

                // Map the Firestore documents to our MenuItem data class
                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(MenuItem::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(items) // Emit the list to the Flow
            }

        // Clean up the listener when the ViewModel stops collecting
        awaitClose { subscription.remove() }
    }

    // 2. CREATE: Add a new menu item
    suspend fun addMenuItem(item: MenuItem) {
        val documentRef = menuCollection.document() // auto generates a new ID
        val newItem = item.copy(id = documentRef.id)
        documentRef.set(newItem).await()
    }

    // 3. UPDATE: Overwrite an existing menu item
    suspend fun updateMenuItem(item: MenuItem) {
        menuCollection.document(item.id).set(item).await()
    }

    // 4. DELETE: Remove a menu item
    suspend fun deleteMenuItem(itemId: String) {
        menuCollection.document(itemId).delete().await()
    }

    /**
     * Uploads an image to imgBB Storage and returns the download URL
     */
    suspend fun uploadImage(uri: Uri, context: Context): Result<String> {
        return ImageUploader.upload(uri, context)
    }

}