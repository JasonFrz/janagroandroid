package com.example.janagroandroid.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.janagroandroid.data.local.SessionManager
import com.example.janagroandroid.data.remote.ApiService
import com.example.janagroandroid.data.remote.SocketManager
import com.example.janagroandroid.data.repository.AppRepository
import com.example.janagroandroid.ui.admin.AdminHomeViewModel
import com.example.janagroandroid.ui.admin.AdminMerchantsViewModel
import com.example.janagroandroid.ui.admin.AdminUsersViewModel
import com.example.janagroandroid.ui.admin.AdminVouchersViewModel
import com.example.janagroandroid.ui.auth.AuthViewModel
import com.example.janagroandroid.ui.cart.CartViewModel
import com.example.janagroandroid.ui.chat.ChatListViewModel
import com.example.janagroandroid.ui.chat.ChatViewModel
import com.example.janagroandroid.ui.checkout.CheckoutViewModel
import com.example.janagroandroid.ui.home.HomeViewModel
import com.example.janagroandroid.ui.history.HistoryViewModel
import com.example.janagroandroid.ui.merchant.MerchantDetailViewModel
import com.example.janagroandroid.ui.profile.ProfileViewModel
import com.example.janagroandroid.ui.product.ProductDetailViewModel
import com.example.janagroandroid.ui.seller.AddProductViewModel
import com.example.janagroandroid.ui.seller.ManageProductsViewModel
import com.example.janagroandroid.ui.seller.SellerDashboardViewModel
import com.example.janagroandroid.ui.splash.SplashViewModel

class AppViewModelFactory(
    private val app: Application? = null,
    private val repo: AppRepository? = null,
    private val apiService: ApiService? = null,
    private val socketManager: SocketManager? = null,
    private val sessionManager: SessionManager? = null,
    private val partnerId: Long? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val vm: ViewModel = when {
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel(app!!, repo!!)
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(repo!!)
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(app!!, repo!!)
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(app!!, repo!!, socketManager!!)
            modelClass.isAssignableFrom(ProductDetailViewModel::class.java) -> ProductDetailViewModel(app!!, repo!!)
            modelClass.isAssignableFrom(CartViewModel::class.java) -> CartViewModel(app!!, repo!!)
            modelClass.isAssignableFrom(CheckoutViewModel::class.java) -> CheckoutViewModel(app!!, repo!!)
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> HistoryViewModel(app!!, repo!!)
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> ProfileViewModel(app!!, repo!!)
            modelClass.isAssignableFrom(SellerDashboardViewModel::class.java) -> SellerDashboardViewModel(app!!, repo!!)
            modelClass.isAssignableFrom(ManageProductsViewModel::class.java) -> ManageProductsViewModel(app!!, repo!!)
            modelClass.isAssignableFrom(AddProductViewModel::class.java) -> AddProductViewModel(app!!, repo!!)
            modelClass.isAssignableFrom(AdminHomeViewModel::class.java) -> AdminHomeViewModel(app!!, repo!!)
            modelClass.isAssignableFrom(AdminUsersViewModel::class.java) -> AdminUsersViewModel(app!!, repo!!)
            modelClass.isAssignableFrom(AdminMerchantsViewModel::class.java) -> AdminMerchantsViewModel(app!!, repo!!)
            modelClass.isAssignableFrom(AdminVouchersViewModel::class.java) -> AdminVouchersViewModel(app!!, repo!!)
            modelClass.isAssignableFrom(MerchantDetailViewModel::class.java) -> MerchantDetailViewModel(app!!, repo!!)
            modelClass.isAssignableFrom(ChatListViewModel::class.java) -> ChatListViewModel(app!!, repo!!)
            modelClass.isAssignableFrom(ChatViewModel::class.java) -> ChatViewModel(apiService!!, socketManager!!, sessionManager!!, partnerId!!)
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        @Suppress("UNCHECKED_CAST")
        return vm as T
    }
}
