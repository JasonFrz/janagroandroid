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
import kotlinx.coroutines.launch

class HomeViewModel(
    app: Application,
    private val repo: AppRepository
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
            }
        }
        fetchCategories()
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
            repo.refreshRemoteProducts()
            fetchCategories()
            fetchTopMerchants()
            fetchActiveVouchers()
            if (repo.isLoggedIn()) {
                repo.refreshProfile()
                repo.getRemoteCart()
            }
        }
    }

    fun fetchActiveVouchers() {
        viewModelScope.launch {
            val result = repo.getActiveVouchers()
            _activeVouchers.postValue(result)
        }
    }

    fun fetchCategories() {
        viewModelScope.launch {
            val result = repo.getCategories()
            _categories.postValue(result)
        }
    }

    fun fetchTopMerchants() {
        viewModelScope.launch {
            val result = repo.getHighestRatedMerchants(6)
            _topMerchants.postValue(result)
        }
    }

    fun addToCart(productId: Long, qty: Int = 1) {
        viewModelScope.launch {
            val (success, errorMsg) = repo.addRemoteCart(productId, qty)
            if (success) {
                _addToCartResult.postValue(Result.success("Produk ditambahkan ke keranjang"))
            } else {
                _addToCartResult.postValue(Result.failure(Exception(errorMsg ?: "Gagal menambahkan ke keranjang")))
            }
        }
    }

    fun clearAddToCartResult() {
        _addToCartResult.value = null
    }
}
