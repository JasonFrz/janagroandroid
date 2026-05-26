package com.example.janagroandroid.ui.checkout

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.launch

class CheckoutViewModel(
    app: Application,
    private val repo: AppRepository
) : AndroidViewModel(app) {

    fun checkout(total: Double) {
        viewModelScope.launch { repo.checkout(total) }
    }

    fun checkout(total: Double, vararg ignored: Any?) {
        viewModelScope.launch { repo.checkout(total) }
    }

    fun clearCart() {
        viewModelScope.launch { repo.clearCart() }
    }
}