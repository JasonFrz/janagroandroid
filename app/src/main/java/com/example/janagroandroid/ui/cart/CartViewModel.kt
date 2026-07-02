package com.example.janagroandroid.ui.cart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.local.entity.CartEntity
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.launch

class CartViewModel(
    app: Application,
    private val repo: AppRepository
) : AndroidViewModel(app) {

    val cart: LiveData<List<CartEntity>> = repo.cart

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchCart() {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try { repo.getRemoteCart() } catch (e: Exception) { e.printStackTrace() }
            finally { _isLoading.postValue(false) }
        }
    }

    fun updateQuantity(id: Long, newQty: Int) {
        viewModelScope.launch {
            try { repo.updateRemoteCart(id, newQty) } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun deleteCart(id: Long) {
        viewModelScope.launch {
            try { repo.removeCartItem(id) } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun deleteAllCart() {
        viewModelScope.launch {
            try { repo.removeAllRemoteCart() } catch (e: Exception) { e.printStackTrace() }
        }
    }
}