package com.example.janagroandroid.ui.cart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.local.entity.CartEntity
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.launch

class CartViewModel(
    app: Application,
    private val repo: AppRepository
) : AndroidViewModel(app) {

    val cart: LiveData<List<CartEntity>> = repo.cart

    fun fetchCart() {
        viewModelScope.launch { repo.getRemoteCart() }
    }

    fun updateQuantity(id: Long, newQty: Int) {
        viewModelScope.launch { repo.updateRemoteCart(id, newQty) }
    }

    fun deleteCart(id: Long) {
        viewModelScope.launch { repo.removeCartItem(id) }
    }

    fun deleteAllCart() {
        viewModelScope.launch { repo.removeAllRemoteCart() }
    }
}