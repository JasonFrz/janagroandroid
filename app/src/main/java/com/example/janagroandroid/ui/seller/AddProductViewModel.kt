package com.example.janagroandroid.ui.seller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.launch

class AddProductViewModel(
    private val repo: AppRepository
) : ViewModel() {

    val currentUserId: Long
        get() = repo.currentUserId

    fun addProduct(
        productId: Long = 0L,
        sellerId: Long = currentUserId,
        name: String,
        category: String,
        price: Double,
        stock: Int,
        imageUrl: String = "",
        description: String = "",
        onDone: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            val success = repo.addProduct(
                productId = productId,
                sellerId = sellerId,
                name = name,
                category = category,
                price = price,
                stock = stock,
                imageUrl = imageUrl,
                description = description
            )
            onDone(success)
        }
    }
}