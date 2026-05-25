package com.example.janagroandroid.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val repo: AppRepository
) : ViewModel() {

    fun checkout(total: Double, onDone: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            onDone(repo.checkout(total))
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repo.clearCart()
        }
    }
}