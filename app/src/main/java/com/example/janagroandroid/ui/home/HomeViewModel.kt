package com.example.janagroandroid.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
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
        // If "All" or null, return all products sorted by newest for the main list too or as default
        val sortedList = list.sortedByDescending { it.createdAt }
        if (category == null || category == "All") return sortedList
        return sortedList.filter { it.category.equals(category, ignoreCase = true) }
    }

    private val _categories = MutableLiveData<List<CategoryDto>>()
    val categories: LiveData<List<CategoryDto>> = _categories

    // Asumsi repo punya variabel untuk mendapatkan user yang sedang aktif
    val currentUser: LiveData<UserEntity?> = repo.getUser

    private val _topMerchants = MutableLiveData<List<MerchantDto>>()
    val topMerchants: LiveData<List<MerchantDto>> = _topMerchants

    private val _activeVouchers = MutableLiveData<List<VoucherDto>>()
    val activeVouchers: LiveData<List<VoucherDto>> = _activeVouchers

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
}
