package com.example.janagroandroid.ui.product

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.local.entity.CartEntity
import com.example.janagroandroid.data.local.entity.ProductEntity
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    app: Application,
    private val repo: AppRepository
) : AndroidViewModel(app) {

    private val _product = MutableLiveData<ProductEntity?>()
    val product: LiveData<ProductEntity?> = _product

    fun fetchProductDetail(id: Long) {
        viewModelScope.launch {
            val result = repo.getRemoteProductDetail(id)
            _product.postValue(result)
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