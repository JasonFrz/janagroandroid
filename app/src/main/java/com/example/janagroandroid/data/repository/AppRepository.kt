package com.example.janagroandroid.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.janagroandroid.data.SessionManager
import com.example.janagroandroid.data.local.dao.CartDao
import com.example.janagroandroid.data.local.dao.HistoryDao
import com.example.janagroandroid.data.local.dao.ProductDao
import com.example.janagroandroid.data.local.dao.UserDao
import com.example.janagroandroid.data.local.entity.CartEntity
import com.example.janagroandroid.data.local.entity.ProductEntity
import com.example.janagroandroid.data.local.entity.TransactionEntity
import com.example.janagroandroid.data.local.entity.UserEntity
import com.example.janagroandroid.data.remote.ApiService
import com.example.janagroandroid.data.remote.dto.AdminStats
import com.example.janagroandroid.data.remote.dto.LoginRequest
import com.example.janagroandroid.data.remote.dto.RegisterRequest
import com.example.janagroandroid.data.remote.dto.toEntity

class AppRepository(
    private val userDao: UserDao,
    private val productDao: ProductDao,
    private val cartDao: CartDao,
    private val historyDao: HistoryDao,
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {
    val getUser: LiveData<UserEntity?> get() = userDao.getCurrentUser()
    val products: LiveData<List<ProductEntity>> get() = productDao.getAll()
    val cart: LiveData<List<CartEntity>> get() = cartDao.getCart(currentUserId.toInt())
    val history: LiveData<List<TransactionEntity>> get() = historyDao.getHistory(currentUserId.toInt())
    val sellerProducts: LiveData<List<ProductEntity>> get() = productDao.getBySeller(currentUserId.toInt())

    val currentUserId: Long
        get() = sessionManager.getUserId().takeIf { it > 0L } ?: 0L

    val isLoggedIn: Boolean
        get() = sessionManager.isLoggedIn()

    suspend fun getCurrentUser(): UserEntity? {
        return userDao.getCurrentUserSync()
    }

    suspend fun login(email: String, password: String): Boolean {
        return try {
            val response = apiService.login(LoginRequest(email = email, password = password))
            if (!response.isSuccessful) return false

            val body = response.body()
            val token = body?.data?.token.orEmpty()
            val remoteUser = body?.data?.user

            if (token.isNotBlank() && remoteUser != null) {
                sessionManager.saveToken(token)
                sessionManager.saveUserId(remoteUser.id)
                userDao.logoutAll()
                userDao.insert(remoteUser.toEntity(password = password, isLoggedIn = true))
                userDao.setLoggedIn(remoteUser.id)
            }
            true
        } catch (e: Exception) {
            Log.e("AppRepository", "Login failed", e)
            false
        }
    }

    suspend fun register(user: UserEntity): Boolean {
        return try {
            val response = apiService.register(
                RegisterRequest(
                    name = user.name,
                    email = user.email,
                    phone = user.phone ?: "",
                    password = user.password
                )
            )
            if (!response.isSuccessful) return false

            val body = response.body()
            val token = body?.data?.token.orEmpty()
            val remoteUser = body?.data?.user

            if (token.isNotBlank() && remoteUser != null) {
                sessionManager.saveToken(token)
                sessionManager.saveUserId(remoteUser.id)
                userDao.insert(remoteUser.toEntity(password = user.password, isLoggedIn = true))
                userDao.setLoggedIn(remoteUser.id)
            }
            true
        } catch (e: Exception) {
            Log.e("AppRepository", "Register failed", e)
            false
        }
    }

    suspend fun logout() {
        runCatching { apiService.logout() }
        sessionManager.logout()
        userDao.logoutAll()
    }

    suspend fun refreshRemoteProducts(): Boolean {
        return try {
            val response = apiService.getProducts()
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("AppRepository", "Refresh products failed", e)
            false
        }
    }

    suspend fun getRemoteProductDetail(id: Long): ProductEntity? {
        return productDao.getById(id.toInt())
    }

    suspend fun getAdminStats(): AdminStats? {
        return try {
            val response = apiService.getAdminStats()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.e("AppRepository", "Get admin stats failed", e)
            null
        }
    }

    suspend fun addToCart(item: CartEntity): Long {
        return cartDao.insert(item)
    }

    suspend fun addToCart(
        productId: Long,
        productName: String,
        price: Double,
        qty: Int,
        imageUrl: String
    ): Boolean {
        val item = CartEntity(
            userId = currentUserId.toInt(),
            productId = productId.toInt(),
            productName = productName,
            price = price,
            qty = qty,
            imageUrl = imageUrl
        )
        cartDao.insert(item)
        return true
    }

    suspend fun deleteCart(cart: CartEntity) {
        cartDao.delete(cart.cartId)
    }

    suspend fun deleteCartById(cartId: Long) {
        cartDao.delete(cartId.toInt())
    }

    suspend fun clearCart(userId: Long? = null) {
        cartDao.clear((userId ?: currentUserId).toInt())
    }

    suspend fun checkout(total: Double = 0.0): Boolean {
        val transaction = TransactionEntity(
            userId = currentUserId.toInt(),
            total = total,
            status = "PAID"
        )
        historyDao.insert(transaction)
        cartDao.clear(currentUserId.toInt())
        return true
    }

    suspend fun checkout(userId: Long, total: Double): Boolean {
        val transaction = TransactionEntity(
            userId = userId.toInt(),
            total = total,
            status = "PAID"
        )
        historyDao.insert(transaction)
        cartDao.clear(userId.toInt())
        return true
    }

    suspend fun addProduct(
        productId: Long = 0L,
        sellerId: Long = currentUserId,
        name: String,
        category: String,
        price: Double,
        stock: Int,
        imageUrl: String = "",
        description: String = ""
    ): Boolean {
        val product = ProductEntity(
            productId = productId.toInt(),
            sellerId = sellerId.toInt(),
            name = name,
            category = category,
            price = price,
            stock = stock,
            imageUrl = imageUrl,
            description = description
        )
        productDao.insert(product)
        return true
    }
}