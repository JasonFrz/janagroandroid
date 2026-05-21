package com.example.janagroandroid.ui.seller

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.local.entity.ProductEntity
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.launch

class AddProductViewModel(
    app: Application,
    private val repo: AppRepository
) : AndroidViewModel(app) {

    val currentUserId: Long = repo.currentUserId()

    fun addProduct(
        name: String,
        price: Double,
        stock: Int,
        imageUrl: String,
        description: String,
        category: String = ""
    ) {
        viewModelScope.launch {
            repo.addProduct(
                ProductEntity(
                    merchant_id = currentUserId,
                    name = name,
                    price = price,
                    stock = stock,
                    imageUrl = imageUrl,
                    description = description,
                    category = category
                )
            )
        }
    }

    fun save(
        name: String,
        price: Double,
        stock: Int,
        imageUrl: String,
        description: String,
        category: String = ""
    ) {
        addProduct(name, price, stock, imageUrl, description, category)
    }
}