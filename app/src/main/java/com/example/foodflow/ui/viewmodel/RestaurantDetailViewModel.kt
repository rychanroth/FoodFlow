package com.example.foodflow.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.AppUser
import com.example.foodflow.data.model.MenuItem
import com.example.foodflow.data.model.Promotion
import com.example.foodflow.data.repository.MenuRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RestaurantDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val menuRepository = MenuRepository()
    private val firestore = FirebaseFirestore.getInstance() // For quick user fetch

    // Extract restaurantId from Navigation arguments
    private val restaurantId: String = savedStateHandle["restaurantId"] ?: ""

    private val _restaurant = MutableStateFlow<AppUser?>(null)
    val restaurant: StateFlow<AppUser?> = _restaurant

    private val _promotions = MutableStateFlow<List<Promotion>>(emptyList())
    val promotions: StateFlow<List<Promotion>> = _promotions

    private val _allMenuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow() // NEW: Expose state

    // Fetch global categories to map IDs to Names for the UI chips
    val categories = menuRepository.getCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered list based on selected category
    val filteredMenuItems = combine(_allMenuItems, _selectedCategory) { items, selectedCatId ->
        if (selectedCatId == null) items else items.filter { it.categoryId == selectedCatId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadRestaurantData()
    }

    private fun loadRestaurantData() {
        // 1. Fetch Restaurant Profile
        viewModelScope.launch {
            val doc = firestore.collection("users").document(restaurantId).get().await()
            _restaurant.value = try { doc.toObject(AppUser::class.java) } catch (e: Exception) { null }
        }

        // 2. Fetch Promotions for this restaurant (local filter)
        viewModelScope.launch {
            menuRepository.getActivePromotions().collect { promos ->
                _promotions.value = promos.filter { it.restaurantId == restaurantId }
            }
        }

        // 3. Fetch Active Menu Items for this restaurant (V3 query)
        viewModelScope.launch {
            menuRepository.getActiveMenuItems(restaurantId).collect { items ->
                _allMenuItems.value = items
            }
        }
    }

    fun selectCategory(categoryId: String?) {
        _selectedCategory.value = categoryId
    }

    // NEW: Derive available categories from the FULL list, not the filtered one
    val availableCategories = combine(_allMenuItems, categories) { allItems, globalCategories ->
        allItems.mapNotNull { item ->
            globalCategories.find { it.id == item.categoryId }
        }.distinctBy { it.id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}