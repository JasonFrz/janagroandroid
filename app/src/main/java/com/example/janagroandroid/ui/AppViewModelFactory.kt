package com.example.janagroandroid.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.janagroandroid.data.repository.AppRepository
import com.example.janagroandroid.ui.admin.AdminHomeViewModel
import com.example.janagroandroid.ui.auth.AuthViewModel
import com.example.janagroandroid.ui.cart.CartViewModel
import com.example.janagroandroid.ui.checkout.CheckoutViewModel
import com.example.janagroandroid.ui.history.HistoryViewModel
import com.example.janagroandroid.ui.home.HomeViewModel
import com.example.janagroandroid.ui.product.ProductDetailViewModel
import com.example.janagroandroid.ui.profile.ProfileViewModel
import com.example.janagroandroid.ui.seller.AddProductViewModel
import com.example.janagroandroid.ui.seller.SellerDashboardViewModel
import com.example.janagroandroid.ui.splash.SplashViewModel

class AppViewModelFactory(
    private val application: Application,
    private val repository: AppRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(application, repository) as T

            modelClass.isAssignableFrom(MainViewModel::class.java) ->
                MainViewModel(repository) as T

            modelClass.isAssignableFrom(SplashViewModel::class.java) ->
                SplashViewModel(repository) as T

            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(repository) as T

            modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
                ProfileViewModel(repository) as T

            modelClass.isAssignableFrom(CartViewModel::class.java) ->
                CartViewModel(repository) as T

            modelClass.isAssignableFrom(CheckoutViewModel::class.java) ->
                CheckoutViewModel(repository) as T

            modelClass.isAssignableFrom(HistoryViewModel::class.java) ->
                HistoryViewModel(repository) as T

            modelClass.isAssignableFrom(ProductDetailViewModel::class.java) ->
                ProductDetailViewModel(repository) as T

            modelClass.isAssignableFrom(AddProductViewModel::class.java) ->
                AddProductViewModel(repository) as T

            modelClass.isAssignableFrom(SellerDashboardViewModel::class.java) ->
                SellerDashboardViewModel(repository) as T

            modelClass.isAssignableFrom(AdminHomeViewModel::class.java) ->
                AdminHomeViewModel(repository) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}