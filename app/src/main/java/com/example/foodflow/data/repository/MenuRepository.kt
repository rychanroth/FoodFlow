package com.example.foodflow.data.repository

import android.content.Context
import android.net.Uri
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.data.model.MenuItemCategory
import com.example.foodflow.data.model.Promotion
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MenuRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val menuCollection = firestore.collection("menu_items")
    private val categoriesCollection = firestore.collection("categories")
    private val promotionsCollection = firestore.collection("promotions")

    // Existing: Restaurant query (shows all items regardless of isActive)
    fun getMenuItems(restaurantId: String): Flow<List<MenuItem>> = callbackFlow {
        val subscription = menuCollection
            .whereEqualTo("restaurantId", restaurantId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val items = snapshot?.documents?.mapNotNull { doc ->
                    try { doc.toObject(MenuItem::class.java)?.copy(id = doc.id) } catch (e: Exception) { null }
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { subscription.remove() }
    }

    fun getMenuItemById(id: String): Flow<MenuItem?> = callbackFlow {
        val docRef = firestore.collection("menu_items").document(id)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val item = snapshot.toObject(MenuItem::class.java)?.copy(id = snapshot.id)
                trySend(item)
            } else {
                // ✅ Send null instead of throwing an exception
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    // NEW V3: Customer query - ONLY active items
    // !! REQUIRES COMPOSITE INDEX: menu_items: restaurantId (ASC), isActive (ASC) !!
    fun getActiveMenuItems(restaurantId: String): Flow<List<MenuItem>> = callbackFlow {
        val subscription = menuCollection
            .whereEqualTo("restaurantId", restaurantId)
            .whereEqualTo("isActive", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val items = snapshot?.documents?.mapNotNull { doc ->
                    try { doc.toObject(MenuItem::class.java)?.copy(id = doc.id) } catch (e: Exception) { null }
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun getMenuItemsByIds(itemIds: List<String>): Result<List<MenuItem>> {
        return try {
            if (itemIds.isEmpty()) {
                Result.success(emptyList())
            } else {
                // Firestore 'in' queries support a maximum of 30 items in the array
                val batchedIds = itemIds.chunked(30)
                val allItems = mutableListOf<MenuItem>()

                for (batch in batchedIds) {
                    val snapshot = menuCollection
                        .whereIn(FieldPath.documentId(), batch)
                        .get()
                        .await()

                    val items = snapshot.documents.mapNotNull { doc ->
                        try { doc.toObject(MenuItem::class.java)?.copy(id = doc.id) } catch (e: Exception) { null }
                    }
                    allItems.addAll(items)
                }
                Result.success(allItems)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // NEW V3: Browse by Category
    // !! REQUIRES COMPOSITE INDEX: menu_items: categoryId (ASC), isActive (ASC) !!
    fun getMenuItemsByCategory(categoryId: String): Flow<List<MenuItem>> = callbackFlow {
        val subscription = menuCollection
            .whereEqualTo("categoryId", categoryId)
            .whereEqualTo("isActive", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val items = snapshot?.documents?.mapNotNull { doc ->
                    try { doc.toObject(MenuItem::class.java)?.copy(id = doc.id) } catch (e: Exception) { null }
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { subscription.remove() }
    }

    // NEW V3: Newly Added Items (for Customer Home)
    // !! REQUIRES COMPOSITE INDEX: menu_items: isActive (ASC), createdAt (DESC) !!
    fun getNewlyAddedItems(limit: Long = 10): Flow<List<MenuItem>> = callbackFlow {
        val subscription = menuCollection
            .whereEqualTo("isActive", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val items = snapshot?.documents?.mapNotNull { doc ->
                    try { doc.toObject(MenuItem::class.java)?.copy(id = doc.id) } catch (e: Exception) { null }
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addMenuItem(item: MenuItem) {
        val documentRef = menuCollection.document()
        val newItem = item.copy(id = documentRef.id)
        documentRef.set(newItem).await()
    }

    suspend fun updateMenuItem(item: MenuItem) {
        menuCollection.document(item.id).set(item).await()
    }

    suspend fun deleteMenuItem(itemId: String) {
        menuCollection.document(itemId).delete().await()
    }

    // Refactored: Delegate to shared ImageUploader
    suspend fun uploadImage(uri: Uri, context: Context): Result<String> {
        return ImageUploader.upload(uri, context)
    }

    // --- CATEGORIES (V3) ---

    fun getCategories(): Flow<List<MenuItemCategory>> = callbackFlow {
        val subscription = categoriesCollection
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val categories = snapshot?.documents?.mapNotNull { doc ->
                    try { doc.toObject(MenuItemCategory::class.java)?.copy(id = doc.id) } catch (e: Exception) { null }
                } ?: emptyList()
                trySend(categories)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addCategory(category: MenuItemCategory) {
        val documentRef = categoriesCollection.document()
        val newCategory = category.copy(id = documentRef.id)
        documentRef.set(newCategory).await()
    }

    suspend fun updateCategory(category: MenuItemCategory) {
        categoriesCollection.document(category.id).set(category).await()
    }

    suspend fun deleteCategory(categoryId: String) {
        categoriesCollection.document(categoryId).delete().await()
    }

    // --- PROMOTIONS (V3) ---

    fun getActivePromotions(): Flow<List<Promotion>> = callbackFlow {
        val subscription = promotionsCollection
            .whereEqualTo("isActive", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val promotions = snapshot?.documents?.mapNotNull { doc ->
                    try { doc.toObject(Promotion::class.java)?.copy(id = doc.id) } catch (e: Exception) { null }
                } ?: emptyList()
                trySend(promotions)
            }
        awaitClose { subscription.remove() }
    }

    // Admin fetch all (including inactive)
    fun getAllPromotions(): Flow<List<Promotion>> = callbackFlow {
        val subscription = promotionsCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val promotions = snapshot?.documents?.mapNotNull { doc ->
                    try { doc.toObject(Promotion::class.java)?.copy(id = doc.id) } catch (e: Exception) { null }
                } ?: emptyList()
                trySend(promotions)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addPromotion(promotion: Promotion) {
        val documentRef = promotionsCollection.document()
        val newPromotion = promotion.copy(id = documentRef.id)
        documentRef.set(newPromotion).await()
    }

    suspend fun updatePromotion(promotion: Promotion) {
        promotionsCollection.document(promotion.id).set(promotion).await()
    }

    suspend fun deletePromotion(promotionId: String) {
        promotionsCollection.document(promotionId).delete().await()
    }

}