package com.example.foodflow.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.MenuItemCategory
import com.example.foodflow.data.model.Promotion
import com.example.foodflow.data.repository.AdminRepository
import com.example.foodflow.data.repository.ImageUploader
import com.example.foodflow.data.repository.MenuRepository
import com.example.foodflow.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val adminRepository = AdminRepository()
    private val menuRepository = MenuRepository() // Re-use for catalog
    private val orderRepository = OrderRepository() // NEW

    private val _pendingApps = MutableStateFlow<List<com.example.foodflow.data.model.RoleApplication>>(emptyList())
    val pendingApps: StateFlow<List<com.example.foodflow.data.model.RoleApplication>> = _pendingApps.asStateFlow()

    private val _categories = MutableStateFlow<List<MenuItemCategory>>(emptyList())
    val categories: StateFlow<List<MenuItemCategory>> = _categories.asStateFlow()

    private val _promotions = MutableStateFlow<List<Promotion>>(emptyList())
    val promotions: StateFlow<List<Promotion>> = _promotions.asStateFlow()
    // NEW: Dashboard Stats
    private val _todayRevenue = MutableStateFlow(0.0)
    val todayRevenue: StateFlow<Double> = _todayRevenue.asStateFlow()

    private val _todayOrders = MutableStateFlow(0)
    val todayOrders: StateFlow<Int> = _todayOrders.asStateFlow()

    private val _totalUsers = MutableStateFlow(0)
    val totalUsers: StateFlow<Int> = _totalUsers.asStateFlow()

    init {
        viewModelScope.launch {
            adminRepository.getPendingApplications().collect { _pendingApps.value = it }
        }
        viewModelScope.launch {
            menuRepository.getCategories().collect { _categories.value = it }
        }
        viewModelScope.launch {
            menuRepository.getAllPromotions().collect { _promotions.value = it }
        }
        loadDashboardStats()
    }

    private fun loadDashboardStats() {
        // 1. Fetch Total Users (One-time fetch, doesn't need real-time for a dashboard counter)
        viewModelScope.launch {
            val result = adminRepository.getTotalUsersCount()
            if (result.isSuccess) {
                _totalUsers.value = result.getOrDefault(0)
            }
        }

        // 2. Fetch Today's Orders and Aggregate locally
        viewModelScope.launch {
            val startOfDay = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val endOfDay = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis

            orderRepository.getPlatformOrders(startOfDay, endOfDay).collect { orders ->
                _todayOrders.value = orders.size
                _todayRevenue.value = orders.sumOf { it.platformEarnings } // Admin sees platform cut
            }
        }
    }

    // --- Applications ---
    fun approveApplication(app: com.example.foodflow.data.model.RoleApplication) {
        viewModelScope.launch { adminRepository.approveApplication(app.id, app.userId, app.requestedRole.name) }
    }

    fun rejectApplication(appId: String) {
        viewModelScope.launch { adminRepository.rejectApplication(appId) }
    }

    // --- Categories ---
    fun addCategory(name: String, imageUri: Uri?) {
        viewModelScope.launch {
            val imageUrl = imageUri?.let {
                ImageUploader.upload(it, getApplication()).getOrNull() ?: ""
            } ?: ""
            menuRepository.addCategory(MenuItemCategory(name = name, imageUrl = imageUrl))
        }
    }

    fun updateCategory(existingCategory: MenuItemCategory, newName: String, newImageUri: Uri?) {
        viewModelScope.launch {
            val finalImageUrl = newImageUri?.let {
                ImageUploader.upload(it, getApplication()).getOrNull() ?: existingCategory.imageUrl
            } ?: existingCategory.imageUrl

            // FIX: Apply newName to the copy!
            menuRepository.updateCategory(existingCategory.copy(name = newName, imageUrl = finalImageUrl))
        }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch { menuRepository.deleteCategory(categoryId) }
    }

    // --- Promotions ---
    fun approvePromotion(promotion: Promotion) {
        viewModelScope.launch {
            menuRepository.updatePromotion(promotion.copy(isActive = true, isRejected = false))
        }
    }

    fun rejectPromotion(promotion: Promotion) {
        viewModelScope.launch {
            // Set isRejected to true so it leaves the pending list!
            menuRepository.updatePromotion(promotion.copy(isRejected = true, isActive = false))
        }
    }
}