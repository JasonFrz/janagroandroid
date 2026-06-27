package com.example.janagroandroid.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.local.entity.CartEntity
import com.example.janagroandroid.data.local.entity.ProductEntity
import com.example.janagroandroid.data.local.entity.UserEntity
import com.example.janagroandroid.data.remote.dto.CategoryDto
import com.example.janagroandroid.data.remote.dto.MerchantDto
import com.example.janagroandroid.data.remote.dto.VoucherDto
import com.example.janagroandroid.data.repository.AppRepository
import com.example.janagroandroid.data.remote.SocketManager
import kotlinx.coroutines.launch

class HomeViewModel(
    app: Application,
    private val repo: AppRepository,
    private val socketManager: SocketManager
) : AndroidViewModel(app) {

    private val _allProducts = repo.products
    private val _selectedCategory = MutableLiveData<String?>(null)
    val selectedCategory: LiveData<String?> = _selectedCategory

    val products = MediatorLiveData<List<ProductEntity>>().apply {
        addSource(_allProducts) { value = filterProducts(it, _selectedCategory.value) }
        addSource(_selectedCategory) { value = filterProducts(_allProducts.value, it) }
    }

    // Recently listed products: top 4 newest products
    val recentlyListed = MediatorLiveData<List<ProductEntity>>().apply {
        addSource(_allProducts) { list ->
            value = list?.sortedByDescending { it.createdAt }?.take(4) ?: emptyList()
        }
    }

    private fun filterProducts(list: List<ProductEntity>?, category: String?): List<ProductEntity> {
        if (list == null) return emptyList()
        val sortedList = list.sortedByDescending { it.createdAt }
        if (category == null || category == "All") return sortedList
        return sortedList.filter { it.category.equals(category, ignoreCase = true) }
    }

    private val _categories = MutableLiveData<List<CategoryDto>>()
    val categories: LiveData<List<CategoryDto>> = _categories

    val currentUser: LiveData<UserEntity?> = repo.getUser

    /** Cart items LiveData — wired to local DB, reflects remote cart after sync */
    val cart: LiveData<List<CartEntity>> = repo.cart

    private val _topMerchants = MutableLiveData<List<MerchantDto>>()
    val topMerchants: LiveData<List<MerchantDto>> = _topMerchants

    private val _activeVouchers = MutableLiveData<List<VoucherDto>>()
    val activeVouchers: LiveData<List<VoucherDto>> = _activeVouchers

    /** One-shot result for add-to-cart feedback (null = idle) */
    private val _addToCartResult = MutableLiveData<Result<String>?>()
    val addToCartResult: LiveData<Result<String>?> = _addToCartResult

    init {
        currentUser.observeForever { user ->
            if (user == null) {
                _topMerchants.postValue(emptyList())
                socketManager.disconnect()
            } else {
                setupSocket()
            }
        }
        fetchCategories()
    }

    private fun setupSocket() {
        socketManager.connect()
        val socket = socketManager.getSocket()
        socket?.off("notification:new")
        socket?.on("notification:new") {
            // Automatically fetch notifications when a new one arrives
            fetchNotifications()
        }
    }

    override fun onCleared() {
        super.onCleared()
        socketManager.disconnect()
    }

    fun selectCategory(category: String) {
        if (category == "All" || _selectedCategory.value == category) {
            _selectedCategory.value = null
        } else {
            _selectedCategory.value = category
        }
    }

    fun refreshRemote() {
        viewModelScope.launch {
            try {
                repo.refreshRemoteProducts()
                fetchCategories()
                fetchTopMerchants()
                fetchActiveVouchers()
                if (repo.isLoggedIn()) {
                    repo.refreshProfile()
                    repo.getRemoteCart()
                    fetchNotifications()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchActiveVouchers() {
        viewModelScope.launch {
            try {
                val result = repo.getActiveVouchers()
                _activeVouchers.postValue(result)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchCategories() {
        viewModelScope.launch {
            try {
                val result = repo.getCategories()
                _categories.postValue(result)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchTopMerchants() {
        viewModelScope.launch {
            try {
                val result = repo.getHighestRatedMerchants(6)
                _topMerchants.postValue(result)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addToCart(productId: Long, qty: Int = 1) {
        viewModelScope.launch {
            try {
                val (success, errorMsg) = repo.addRemoteCart(productId, qty)
                if (success) {
                    _addToCartResult.postValue(Result.success("Produk ditambahkan ke keranjang"))
                } else {
                    _addToCartResult.postValue(Result.failure(Exception(errorMsg ?: "Gagal menambahkan ke keranjang")))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _addToCartResult.postValue(Result.failure(Exception("Terjadi kesalahan jaringan")))
            }
        }
    }

    fun clearAddToCartResult() {
        _addToCartResult.value = null
    }

    private val _notifications = MutableLiveData<List<com.example.janagroandroid.data.remote.dto.NotificationDto>>()
    val notifications: LiveData<List<com.example.janagroandroid.data.remote.dto.NotificationDto>> = _notifications

    fun fetchNotifications() {
        viewModelScope.launch {
            try {
                if (repo.isLoggedIn()) {
                    val result = repo.getNotifications()
                    _notifications.postValue(result)
                } else {
                    _notifications.postValue(emptyList())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun markNotificationsAsRead() {
        viewModelScope.launch {
            try {
                if (repo.isLoggedIn()) {
                    val success = repo.readNotifications()
                    if (success) {
                        fetchNotifications()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
