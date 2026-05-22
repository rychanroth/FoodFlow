package com.example.foodflow.data.repository

import android.content.Context
import android.net.Uri
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
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
        return try {
            withContext(Dispatchers.IO) { // Network and file reading MUST be on IO thread
                // 1. Read the image from the phone's storage into bytes
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: throw Exception("Cannot open image")
                val bytes = inputStream.readBytes()
                inputStream.close()

                // 2. Convert bytes to Base64 string (ImgBB requires Base64 for simple uploads)
                val base64Image = Base64.encodeToString(bytes, Base64.DEFAULT)

                // 3. Build the HTTP Request to ImgBB
                val client = OkHttpClient()
                val requestBody = FormBody.Builder()
                    .add("image", base64Image)
                    .build()

                // PASTE YOUR IMGBB API KEY HERE
                val apiKey = "275d5d29386d3304ee6cdd1f5625194c"

                val request = Request.Builder()
                    .url("https://api.imgbb.com/1/upload?key=$apiKey")
                    .post(requestBody)
                    .build()

                // 4. Execute the request
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (!response.isSuccessful || responseBody == null) {
                    throw Exception("ImgBB upload failed: ${response.code}")
                }

                // 5. Parse the JSON response to get the direct image URL
                val json = JSONObject(responseBody)
                val imageUrl = json.getJSONObject("data").getString("url")

                Result.success(imageUrl)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}