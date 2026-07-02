package com.example.janagroandroid.ui.seller

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.local.entity.ProductEntity
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.launch

class ManageProductsViewModel(
    app: Application,
    private val repo: AppRepository
) : AndroidViewModel(app) {

    private val _products = MutableLiveData<List<ProductEntity>>()
    val products: LiveData<List<ProductEntity>> = _products

    private val _deleteStatus = MutableLiveData<Boolean?>()
    val deleteStatus: LiveData<Boolean?> = _deleteStatus

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun loadProducts() {
        viewModelScope.launch {
            try {
                val result = repo.getCurrentSellerProducts()
                _products.postValue(result)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            try {
                val success = repo.deleteRemoteProduct(productId)
                _deleteStatus.postValue(success)
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.postValue(e.message ?: "Terjadi kesalahan tidak terduga")
                _deleteStatus.postValue(false)
            }
        }
    }
}
