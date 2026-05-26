package com.example.janagroandroid.ui.seller

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.janagroandroid.data.local.entity.ProductEntity
import com.example.janagroandroid.data.repository.AppRepository

class SellerDashboardViewModel(
    app: Application,
    private val repo: AppRepository
) : AndroidViewModel(app) {
    val sellerProducts: LiveData<List<ProductEntity>> = repo.sellerProducts
    val currentUserId: Long = repo.currentUserId()
}