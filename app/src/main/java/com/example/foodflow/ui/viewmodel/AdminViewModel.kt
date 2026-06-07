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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val adminRepository = AdminRepository()
    private val menuRepository = MenuRepository() // Re-use for catalog

    private val _pendingApps = MutableStateFlow<List<com.example.foodflow.data.model.RoleApplication>>(emptyList())
    val pendingApps: StateFlow<List<com.example.foodflow.data.model.RoleApplication>> = _pendingApps.asStateFlow()

    private val _categories = MutableStateFlow<List<MenuItemCategory>>(emptyList())
    val categories: StateFlow<List<MenuItemCategory>> = _categories.asStateFlow()

    private val _promotions = MutableStateFlow<List<Promotion>>(emptyList())
    val promotions: StateFlow<List<Promotion>> = _promotions.asStateFlow()

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