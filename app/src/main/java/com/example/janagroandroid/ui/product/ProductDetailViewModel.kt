package com.example.janagroandroid.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.local.entity.ProductEntity
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val repo: AppRepository
) : ViewModel() {

    private val _product = MutableStateFlow<ProductEntity?>(null)
    val product: StateFlow<ProductEntity?> = _product.asStateFlow()

    fun loadProductDetail(id: Long) {
        viewModelScope.launch {
            _product.value = repo.getRemoteProductDetail(id)
        }
    }

    fun addToCart(product: ProductEntity, qty: Int = 1) {
        viewModelScope.launch {
            repo.addToCart(
                productId = product.productId.toLong(),
                productName = product.name,
                price = product.price,
                qty = qty,
                imageUrl = product.imageUrl
            )
        }
    }
}