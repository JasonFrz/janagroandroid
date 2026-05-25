package com.example.janagroandroid.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.local.entity.CartEntity
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.launch

class CartViewModel(
    private val repo: AppRepository
) : ViewModel() {

    val cart: LiveData<List<CartEntity>> = repo.cart

    fun deleteCart(item: CartEntity) {
        viewModelScope.launch {
            repo.deleteCart(item)
        }
    }

    fun deleteCartById(cartId: Long) {
        viewModelScope.launch {
            repo.deleteCartById(cartId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repo.clearCart()
        }
    }
}