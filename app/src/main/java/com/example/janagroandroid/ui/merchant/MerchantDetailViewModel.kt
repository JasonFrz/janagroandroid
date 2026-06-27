package com.example.janagroandroid.ui.merchant

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.remote.dto.MerchantDto
import com.example.janagroandroid.data.remote.dto.ProductDto
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.launch

class MerchantDetailViewModel(
    app: Application,
    private val repo: AppRepository
) : AndroidViewModel(app) {

    private val _merchant = MutableLiveData<MerchantDto?>()
    val merchant: LiveData<MerchantDto?> = _merchant

    private val _products = MutableLiveData<List<ProductDto>>()
    private var allProducts = listOf<ProductDto>()
    val products: LiveData<List<ProductDto>> = _products

    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> = _searchQuery

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadMerchantData(merchantId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val merchantData = repo.getRemoteMerchantDetail(merchantId)
                
                _merchant.postValue(merchantData)
                allProducts = merchantData?.products ?: emptyList()
                filterProducts(_searchQuery.value ?: "")
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        filterProducts(query)
    }

    private fun filterProducts(query: String) {
        if (query.isBlank()) {
            _products.value = allProducts
        } else {
            _products.value = allProducts.filter {
                it.name?.contains(query, ignoreCase = true) == true ||
                it.description?.contains(query, ignoreCase = true) == true
            }
        }
    }
}
