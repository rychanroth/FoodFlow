package com.example.foodflow.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.Address
import com.example.foodflow.data.repository.ImageUploader
import com.example.foodflow.data.repository.ProfileRepository
import com.example.foodflow.ui.state.ProfileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProfileRepository(getApplication())

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating.asStateFlow()


    private val _addressDialogState = MutableStateFlow<Address?>(null)
    val addressDialogState: StateFlow<Address?> = _addressDialogState.asStateFlow()

    fun showAddressDialog(address: Address? = null) {
        _addressDialogState.value = address ?: Address() // Null means new address
    }

    fun hideAddressDialog() {
        _addressDialogState.value = null
    }

    fun saveAddress(userId: String, street: String, city: String, isDefault: Boolean, existingAddressId: String?) {
        viewModelScope.launch {
            val currentAddresses = (profileState.value as? ProfileState.Success)?.user?.addresses?.toMutableList() ?: mutableListOf()

            val newAddress = Address(
                id = existingAddressId ?: UUID.randomUUID().toString(),
                street = street,
                city = city,
                isDefault = isDefault
            )

            val existingIndex = currentAddresses.indexOfFirst { it.id == newAddress.id }
            if (existingIndex != -1) {
                currentAddresses[existingIndex] = newAddress
            } else {
                currentAddresses.add(newAddress)
            }

            // If this is the new default, unset others
            if (isDefault) {
                currentAddresses.forEachIndexed { index, addr ->
                    currentAddresses[index] = addr.copy(isDefault = addr.id == newAddress.id)
                }
            } else if (currentAddresses.size == 1) {
                // First address is always default
                currentAddresses[0] = currentAddresses[0].copy(isDefault = true)
            }

            // Convert to Maps for Firestore
            val addressMaps = currentAddresses.map { addr ->
                mapOf(
                    "id" to addr.id,
                    "street" to addr.street,
                    "city" to addr.city,
                    "isDefault" to addr.isDefault
                )
            }

            repository.updateAddresses(userId, addressMaps)
            loadUserProfile(userId) // Refresh data
            hideAddressDialog()
        }
    }

    fun deleteAddress(userId: String, addressId: String) {
        viewModelScope.launch {
            val currentAddresses = (profileState.value as? ProfileState.Success)?.user?.addresses?.toMutableList() ?: mutableListOf()
            val updatedAddresses = currentAddresses.filter { it.id != addressId }.toMutableList()

            // If we deleted the default, make the first one default
            if (currentAddresses.any { it.id == addressId && it.isDefault } && updatedAddresses.isNotEmpty()) {
                updatedAddresses[0] = updatedAddresses[0].copy(isDefault = true)
            }

            val addressMaps = updatedAddresses.map { addr ->
                mapOf("id" to addr.id, "street" to addr.street, "city" to addr.city, "isDefault" to addr.isDefault)
            }

            repository.updateAddresses(userId, addressMaps)
            loadUserProfile(userId)
        }
    }

    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            val result = repository.getUserProfile(userId)
            if (result.isSuccess) {
                _profileState.value = ProfileState.Success(result.getOrNull()!!)
            } else {
                _profileState.value = ProfileState.Error(result.exceptionOrNull()?.message ?: "Failed to load profile")
            }
        }
    }

    fun updateUserProfile(userId: String, name: String, phone: String) {
        viewModelScope.launch {
            _isUpdating.value = true
            val updates = mapOf(
                "name" to name.trim(),
                "phone" to phone.trim()
            )
            repository.updateUserProfile(userId, updates)
            loadUserProfile(userId) // Refresh data
            _isUpdating.value = false
        }
    }

    fun uploadAvatar(userId: String, imageUri: Uri) {
        viewModelScope.launch {
            _isUpdating.value = true
            val uploadResult = ImageUploader.upload(imageUri, getApplication<Application>())
            if (uploadResult.isSuccess) {
                val imageUrl = uploadResult.getOrNull()!!
                repository.updateUserProfile(userId, mapOf("avatarUrl" to imageUrl))
                loadUserProfile(userId)
            }
            _isUpdating.value = false
        }
    }
}