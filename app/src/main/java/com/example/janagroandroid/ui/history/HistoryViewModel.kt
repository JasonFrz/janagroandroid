package com.example.janagroandroid.ui.history

import android.app.Application
import androidx.lifecycle.*
import com.example.janagroandroid.data.remote.dto.CategoryDto
import com.example.janagroandroid.data.remote.dto.OrderDto
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class HistoryViewModel(
    app: Application,
    private val repo: AppRepository
) : AndroidViewModel(app) {
    
    private val _orders = MutableLiveData<List<OrderDto>>()
    val orders: LiveData<List<OrderDto>> = _orders

    private val _categories = MutableLiveData<List<CategoryDto>>()
    val categories: LiveData<List<CategoryDto>> = _categories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _buyAgainStatus = MutableLiveData<Boolean?>()
    val buyAgainStatus: LiveData<Boolean?> = _buyAgainStatus

    fun fetchOrders() {
        viewModelScope.launch {
            _isLoading.postValue(true)
            
            // 1. Get categories for mapping
            val categoryList = repo.getCategories()
            _categories.postValue(categoryList)

            // 2. Get order summary
            val summaryOrders = repo.getRemoteOrders()
            
            // 3. Fetch full detail for EACH order to get complete item & product data
            val detailedOrders = summaryOrders.map { summary ->
                async { repo.getRemoteOrderDetail(summary.id) ?: summary }
            }.awaitAll()

            _orders.postValue(detailedOrders)
            _isLoading.postValue(false)
        }
    }

    fun buyAgain(orderId: Long) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            val success = repo.buyAgain(orderId)
            _buyAgainStatus.postValue(success)
            _isLoading.postValue(false)
        }
    }

    fun resetBuyAgainStatus() {
        _buyAgainStatus.value = null
    }
}
