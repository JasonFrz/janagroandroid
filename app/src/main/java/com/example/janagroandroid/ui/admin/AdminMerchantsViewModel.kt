package com.example.janagroandroid.ui.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.remote.dto.MerchantDto
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.launch

class AdminMerchantsViewModel(
    app: Application,
    private val repo: AppRepository,
) : AndroidViewModel(app){
    private val _merchants = MutableLiveData<List<MerchantDto>>(emptyList())
    val merchants: LiveData<List<MerchantDto>> = _merchants

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String?>(null)
    val message: LiveData<String?> = _message

    fun loadPending(){
        viewModelScope.launch {
            _isLoading.postValue(true)
            _merchants.postValue(repo.getPendingMerchants())
            _isLoading.postValue(false)
        }
    }

    fun updateStatus(id: Long, status: String){
        viewModelScope.launch {
            val success = repo.updateMerchantStatus(id, status)
            if(success){
                _message.postValue("Merchant berhasil di-$status")
                loadPending()
            }else{
                _message.postValue("Gagal mengubah status merchant")
            }
        }
    }

    fun clearMessage(){
        _message.postValue(null)
    }

}