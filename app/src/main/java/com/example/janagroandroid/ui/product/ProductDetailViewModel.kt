package com.example.janagroandroid.ui.product

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.local.entity.ProductEntity
import com.example.janagroandroid.data.remote.dto.ReviewDto
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    app: Application,
    private val repo: AppRepository
) : AndroidViewModel(app) {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _aiTips = MutableLiveData<String?>()
    val aiTips: LiveData<String?> = _aiTips

    private val _isAiLoading = MutableLiveData<Boolean>()
    val isAiLoading: LiveData<Boolean> = _isAiLoading

    private val _product = MutableLiveData<ProductEntity?>()
    val product: LiveData<ProductEntity?> = _product

    private val _reviews = MutableLiveData<List<ReviewDto>>()
    val reviews: LiveData<List<ReviewDto>> = _reviews
    
    private val _activeNegotiation = MutableLiveData<com.example.janagroandroid.data.remote.dto.ActiveNegotiationData?>()
    val activeNegotiation: LiveData<com.example.janagroandroid.data.remote.dto.ActiveNegotiationData?> = _activeNegotiation

    /** null = idle, non-null = message to show (success or error) */
    private val _addToCartResult = MutableLiveData<Result<String>?>()
    val addToCartResult: LiveData<Result<String>?> = _addToCartResult

    fun fetchProductDetail(id: Long) {
        viewModelScope.launch {
            try {
                val result = repo.getRemoteProductDetail(id)
                _product.postValue(result)

                val reviewList = repo.getProductReviews(id)
                _reviews.postValue(reviewList)
                
                val negotiation = repo.getActiveNegotiation(id)
                _activeNegotiation.postValue(negotiation)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addToCart(productId: Long, qty: Int = 1) {
        viewModelScope.launch {
            try {
                val result = repo.addRemoteCart(productId, qty)
                if (result.first) {
                    _addToCartResult.postValue(Result.success("Produk berhasil ditambahkan ke keranjang"))
                } else {
                    _addToCartResult.postValue(Result.failure(Exception(result.second ?: "Gagal menambahkan produk")))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _addToCartResult.postValue(Result.failure(Exception("Terjadi kesalahan jaringan")))
            }
        }
    }

    fun fetchAiTips(productId: Long) {
        viewModelScope.launch {
            _isAiLoading.value = true
            try {
                val tips = repo.getProductAiTips(productId)
                _aiTips.postValue(tips)
            } catch (e: Exception) {
                e.printStackTrace()
                _aiTips.postValue(null)
            } finally {
                _isAiLoading.value = false
            }
        }
    }

    fun clearAddToCartResult() {
        _addToCartResult.value = null
    }
}