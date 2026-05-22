package com.example.foodflow.data.repository

import android.net.Uri
import com.example.foodflow.data.model.MenuItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MenuRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
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
     * Uploads an image to Firebase Storage and returns the download URL
     */
    suspend fun uploadImage(uri: Uri): Result<String> {
        return try {
            // 1. Create a unique filename using current time
            val fileName = "menu_images/${System.currentTimeMillis()}.jpg"
            val storageRef = storage.getReference(fileName)

            // 2. Upload the file
            storageRef.putFile(uri).await()

            // 3. Get the downloadable URL
            val downloadUrl = storageRef.downloadUrl.await().toString()

            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}