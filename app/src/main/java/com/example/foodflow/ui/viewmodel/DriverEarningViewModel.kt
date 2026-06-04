package com.example.foodflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodflow.data.model.Order
import com.example.foodflow.data.repository.OrderRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

// Enum to track which chip is selected
enum class DateRange { TODAY, THIS_WEEK, THIS_MONTH }

class DriverEarningsViewModel : ViewModel() {

    private val repository = OrderRepository()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _totalEarnings = MutableStateFlow(0.0)
    val totalEarnings: StateFlow<Double> = _totalEarnings.asStateFlow()

    private val _totalDeliveries = MutableStateFlow(0)
    val totalDeliveries: StateFlow<Int> = _totalDeliveries.asStateFlow()

    private val _selectedRange = MutableStateFlow(DateRange.THIS_MONTH)
    val selectedRange: StateFlow<DateRange> = _selectedRange.asStateFlow()

    private var earningsJob: Job? = null

    fun loadEarnings(driverId: String) {
        // Cancel the old listener before starting a new one!
        earningsJob?.cancel()

        val (start, end) = getDateRange(_selectedRange.value)

        earningsJob = viewModelScope.launch {
            repository.getThisDriverEarnings(driverId, start, end).collect { ordersList ->
                _orders.value = ordersList
                _totalDeliveries.value = ordersList.size
                _totalEarnings.value = ordersList.sumOf { it.driverEarnings }
            }
        }
    }

    fun updateDateRange(driverId: String, range: DateRange) {
        _selectedRange.value = range
        loadEarnings(driverId)
    }

    private fun getDateRange(range: DateRange): Pair<Long, Long> {
        val end = System.currentTimeMillis()
        val start = when (range) {
            DateRange.TODAY -> getStartOfDay()
            DateRange.THIS_WEEK -> getStartOfWeek()
            DateRange.THIS_MONTH -> getStartOfMonth()
        }
        return Pair(start, end)
    }

    private fun getStartOfDay(): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private fun getStartOfWeek(): Long = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_WEEK, firstDayOfWeek); set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private fun getStartOfMonth(): Long = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1); set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}