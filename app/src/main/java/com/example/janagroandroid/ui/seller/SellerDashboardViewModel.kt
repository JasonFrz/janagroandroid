package com.example.janagroandroid.ui.seller

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.janagroandroid.data.local.entity.ProductEntity
import com.example.janagroandroid.data.repository.AppRepository

class SellerDashboardViewModel(
    private val repo: AppRepository
) : ViewModel() {

    val sellerProducts: LiveData<List<ProductEntity>> = repo.sellerProducts

    val currentUserId: Long
        get() = repo.currentUserId
}