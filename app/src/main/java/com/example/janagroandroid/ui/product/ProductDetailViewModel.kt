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

    private val _product = MutableLiveData<ProductEntity?>()
    val product: LiveData<ProductEntity?> = _product

    private val _reviews = MutableLiveData<List<ReviewDto>>()
    val reviews: LiveData<List<ReviewDto>> = _reviews

    /** null = idle, non-null = message to show (success or error) */
    private val _addToCartResult = MutableLiveData<Result<String>?>()
    val addToCartResult: LiveData<Result<String>?> = _addToCartResult

    fun fetchProductDetail(id: Long) {
        viewModelScope.launch {
            val result = repo.getRemoteProductDetail(id)
            _product.postValue(result)

            val reviewList = repo.getProductReviews(id)
            _reviews.postValue(reviewList)
        }
    }

    fun addToCart(productId: Long, qty: Int = 1) {
        viewModelScope.launch {
            val result = repo.addRemoteCart(productId, qty)
            if (result.first) {
                _addToCartResult.postValue(Result.success("Produk berhasil ditambahkan ke keranjang"))
            } else {
                _addToCartResult.postValue(Result.failure(Exception(result.second ?: "Gagal menambahkan ke keranjang")))
            }
        }
    }

    fun clearAddToCartResult() {
        _addToCartResult.value = null
    }
}