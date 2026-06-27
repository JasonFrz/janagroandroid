package com.example.janagroandroid.ui.seller

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.remote.dto.OrderDto
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.launch

class SellerOrdersViewModel(
    app: Application,
    private val repo: AppRepository
) : AndroidViewModel(app) {

    private val _orders = MutableLiveData<List<OrderDto>>(emptyList())
    val orders: LiveData<List<OrderDto>> = _orders

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _updateStatus = MutableLiveData<Boolean?>()
    val updateStatus: LiveData<Boolean?> = _updateStatus

    fun loadOrders() {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val result = repo.getMerchantOrders()
                _orders.postValue(result)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    /**
     * Advance an order's shipping status. Allowed values: Packed, Shipped, Completed.
     * Refreshes the list on success.
     */
    fun updateStatus(orderId: Long, status: String) {
        viewModelScope.launch {
            try {
                _isLoading.postValue(true)
                val success = repo.updateShippingStatus(orderId, status)
                if (success) {
                    loadOrders()
                } else {
                    _isLoading.postValue(false)
                }
                _updateStatus.postValue(success)
            } catch (e: Exception) {
                e.printStackTrace()
                _isLoading.postValue(false)
                _updateStatus.postValue(false)
            }
        }
    }

    fun resetUpdateStatus() {
        _updateStatus.value = null
    }
}
