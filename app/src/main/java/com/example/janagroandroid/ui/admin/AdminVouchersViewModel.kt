package com.example.janagroandroid.ui.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.remote.dto.VoucherDto
import com.example.janagroandroid.data.remote.dto.VoucherRequest
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.launch

class AdminVouchersViewModel(
    app: Application,
    private val repo: AppRepository
) : AndroidViewModel(app) {

    private val _vouchers = MutableLiveData<List<VoucherDto>>(emptyList())
    val vouchers: MutableLiveData<List<VoucherDto>> = _vouchers

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadVouchers(){
        viewModelScope.launch {
            _isLoading.postValue(true)
            _vouchers.postValue(repo.getVouchers())
            _isLoading.postValue(false)
        }
    }

    fun createVoucher(request: VoucherRequest){
        viewModelScope.launch {
            repo.createVouchers(request)
            _vouchers.postValue(repo.getVouchers())
        }
    }

    fun updateVoucher(id: Long, request: VoucherRequest){
        viewModelScope.launch {
            repo.updateVouchers(id, request)
            _vouchers.postValue(repo.getVouchers())
        }
    }

    fun deleteVoucher(id: Long){
        viewModelScope.launch {
            repo.deleteVouchers(id)
            _vouchers.postValue(repo.getVouchers())
        }
    }
}