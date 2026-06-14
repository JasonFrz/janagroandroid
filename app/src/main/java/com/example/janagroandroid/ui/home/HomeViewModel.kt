package com.example.janagroandroid.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.local.entity.ProductEntity
import com.example.janagroandroid.data.local.entity.UserEntity
import com.example.janagroandroid.data.remote.dto.CategoryDto
import com.example.janagroandroid.data.remote.dto.MerchantDto
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    app: Application,
    private val repo: AppRepository
) : AndroidViewModel(app) {

    val products: LiveData<List<ProductEntity>> = repo.products

    private val _categories = MutableLiveData<List<CategoryDto>>()
    val categories: LiveData<List<CategoryDto>> = _categories

    // Asumsi repo punya variabel untuk mendapatkan user yang sedang aktif
    val currentUser: LiveData<UserEntity?> = repo.getUser

    private val _topMerchants = MutableLiveData<List<MerchantDto>>()
    val topMerchants: LiveData<List<MerchantDto>> = _topMerchants

    init {
        currentUser.observeForever { user ->
            if (user == null) {
                _topMerchants.postValue(emptyList())
            }
        }
        fetchCategories()
    }

    fun refreshRemote() {
        viewModelScope.launch {
            repo.refreshRemoteProducts()
            fetchCategories()
            if (repo.isLoggedIn()) {
                repo.refreshProfile()
                fetchTopMerchants()
            }
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
