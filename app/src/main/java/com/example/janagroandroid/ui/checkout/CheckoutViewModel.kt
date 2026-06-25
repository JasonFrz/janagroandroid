package com.example.janagroandroid.ui.checkout

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.launch

import com.example.janagroandroid.data.local.entity.CartEntity

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

    private val _items = MutableLiveData<List<CartEntity>>(emptyList())
    val items: LiveData<List<CartEntity>> = _items

    private val _selectedVoucher = MutableLiveData<String?>(null)
    val selectedVoucher: LiveData<String?> = _selectedVoucher

    val subtotal: LiveData<Double> = MutableLiveData(0.0)
    val discount: LiveData<Double> = MutableLiveData(0.0)
    val finalTotal: LiveData<Double> = MutableLiveData(0.0)

    fun loadItems(ids: LongArray) {
        viewModelScope.launch {
            val list = repo.getCartItemsByIds(ids)
            _items.value = list
            updateCalculations(list, _selectedVoucher.value)
        }
    }

    fun toggleVoucher(code: String) {
        val current = _selectedVoucher.value
        val newVoucher = if (current == code) null else code
        _selectedVoucher.value = newVoucher
        updateCalculations(_items.value.orEmpty(), newVoucher)
    }

    private fun updateCalculations(list: List<CartEntity>, voucher: String?) {
        val sub = list.sumOf { it.price * it.qty }
        (subtotal as MutableLiveData).value = sub

        val disc = when (voucher) {
            "GRATISONGKIR" -> 10000.0 // Flat Rp 10rb discount
            "DISKON10" -> sub * 0.10  // 10% discount
            else -> 0.0
        }
        (discount as MutableLiveData).value = disc
        (finalTotal as MutableLiveData).value = Math.max(0.0, sub - disc)
    }

    fun checkout(
        shippingAddress: String,
        courier: String? = null,
        paymentType: String? = null
    ) {
        viewModelScope.launch {
            _state.value = CheckoutState.Loading
            val (success, message) = repo.checkout(
                shippingAddress, courier, _selectedVoucher.value, paymentType
            )
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
