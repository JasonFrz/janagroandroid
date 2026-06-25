package com.example.janagroandroid.ui.checkout

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.launch

class CheckoutViewModel(
    app: Application,
    private val repo: AppRepository
) : AndroidViewModel(app) {

    sealed class CheckoutState {
        object Idle : CheckoutState()
        object Loading : CheckoutState()
        data class Success(val message: String?) : CheckoutState()
        data class Error(val message: String?) : CheckoutState()
    }

    private val _state = MutableLiveData<CheckoutState>(CheckoutState.Idle)
    val state: LiveData<CheckoutState> = _state

    fun checkout(
        shippingAddress: String,
        courier: String? = null,
        voucherCode: String? = null,
        paymentType: String? = null
    ) {
        viewModelScope.launch {
            _state.value = CheckoutState.Loading
            val (success, message) = repo.checkout(shippingAddress, courier, voucherCode, paymentType)
            _state.value = if (success) {
                CheckoutState.Success(message)
            } else {
                CheckoutState.Error(message)
            }
        }
    }

    fun resetState() {
        _state.value = CheckoutState.Idle
    }
}
