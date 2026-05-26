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

    fun delete(item: CartEntity) {
        viewModelScope.launch { repo.deleteCart(item) }
    }

    fun deleteCart(item: CartEntity) {
        viewModelScope.launch { repo.deleteCart(item) }
    }

    fun deleteCart(id: Long) {
        viewModelScope.launch { repo.deleteCartById(id) }
    }
}