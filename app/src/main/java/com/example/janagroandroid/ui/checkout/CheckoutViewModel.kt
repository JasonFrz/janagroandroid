package com.example.janagroandroid.ui.checkout

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.launch

import com.example.janagroandroid.data.local.entity.CartEntity
import com.example.janagroandroid.data.remote.dto.VoucherDto

class CheckoutViewModel(
    app: Application,
    private val repo: AppRepository
) : AndroidViewModel(app) {

    sealed class CheckoutState {
        object Idle : CheckoutState()
        object Loading : CheckoutState()
        data class Success(val response: com.example.janagroandroid.data.remote.dto.OrderResponse?) : CheckoutState()
        data class Error(val message: String?) : CheckoutState()
    }

    private val _state = MutableLiveData<CheckoutState>(CheckoutState.Idle)
    val state: LiveData<CheckoutState> = _state

    private val _items = MutableLiveData<List<CartEntity>>(emptyList())
    val items: LiveData<List<CartEntity>> = _items

    private val _activeVouchers = MutableLiveData<List<VoucherDto>>(emptyList())
    val activeVouchers: LiveData<List<VoucherDto>> = _activeVouchers

    private val _userAddress = MutableLiveData<String>("")
    val userAddress: LiveData<String> = _userAddress

    private var selectedItemIds: LongArray = longArrayOf()

    private val _selectedVoucher = MutableLiveData<VoucherDto?>(null)
    val selectedVoucher: LiveData<VoucherDto?> = _selectedVoucher

    val subtotal: LiveData<Double> = MutableLiveData(0.0)
    val discount: LiveData<Double> = MutableLiveData(0.0)
    val finalTotal: LiveData<Double> = MutableLiveData(0.0)

    fun loadInitialData(ids: LongArray) {
        selectedItemIds = ids
        viewModelScope.launch {
            val list = repo.getCartItemsByIds(ids)
            _items.value = list
            updateCalculations(list, _selectedVoucher.value)

            val vouchers = repo.getActiveVouchers()
            _activeVouchers.value = vouchers

            // UserEntity currently does not have an address field. 
            // Default to empty string; user will fill it in the UI.
            _userAddress.value = ""
        }
    }

    fun selectVoucher(voucher: VoucherDto?) {
        _selectedVoucher.value = voucher
        updateCalculations(_items.value.orEmpty(), voucher)
    }

    private fun updateCalculations(list: List<CartEntity>, voucher: VoucherDto?) {
        val sub = list.sumOf { it.price * it.qty }
        (subtotal as MutableLiveData).value = sub

        var disc = 0.0
        if (voucher != null) {
            if (voucher.discountType.equals("Percentage", ignoreCase = true)) {
                val percentage = voucher.discountValue.toDoubleOrNull() ?: 0.0
                disc = sub * (percentage / 100.0)
                val maxDisc = voucher.maxDiscount?.toDoubleOrNull()
                if (maxDisc != null && disc > maxDisc) {
                    disc = maxDisc
                }
            } else if (voucher.discountType.equals("Fixed_Amount", ignoreCase = true)) {
                disc = voucher.discountValue.toDoubleOrNull() ?: 0.0
            }
        }
        
        // Cap discount at subtotal
        if (disc > sub) disc = sub
        
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
            val (success, response) = repo.checkout(
                shippingAddress, courier, _selectedVoucher.value?.code, paymentType, selectedItemIds
            )
            _state.value = if (success) {
                CheckoutState.Success(response)
            } else {
                CheckoutState.Error(response?.message)
            }
        }
    }

    fun resetState() {
        _state.value = CheckoutState.Idle
    }
}
