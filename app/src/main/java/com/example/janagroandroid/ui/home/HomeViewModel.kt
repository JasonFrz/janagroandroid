package com.example.janagroandroid.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.local.entity.ProductEntity
import com.example.janagroandroid.data.local.entity.UserEntity
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    app: Application,
    private val repo: AppRepository
) : AndroidViewModel(app) {

    val products: LiveData<List<ProductEntity>> = repo.products

    // Asumsi repo punya variabel untuk mendapatkan user yang sedang aktif
    val currentUser: LiveData<UserEntity?> = repo.getUser

    fun refreshRemote() {
        viewModelScope.launch {
            repo.refreshRemoteProducts()
        }
    }
}