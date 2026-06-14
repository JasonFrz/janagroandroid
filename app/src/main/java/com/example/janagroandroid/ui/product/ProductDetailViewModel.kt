package com.example.janagroandroid.ui.product

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.local.entity.CartEntity
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

    fun fetchProductDetail(id: Long) {
        viewModelScope.launch {
            val result = repo.getRemoteProductDetail(id)
            _product.postValue(result)
            
            val reviewList = repo.getProductReviews(id)
            _reviews.postValue(reviewList)
        }
    }

    fun addToCart(product: ProductEntity) {
        viewModelScope.launch {
            repo.addRemoteCart(product.id, 1)
        }
    }

    fun addToCart(
        productId: Long,
        productName: String,
        price: Double,
        imageUrl: String,
        qty: Int = 1
    ) {
        viewModelScope.launch {
            repo.addRemoteCart(productId, qty)
        }
    }
}