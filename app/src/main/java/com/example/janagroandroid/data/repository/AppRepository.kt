package com.example.janagroandroid.data.repository

import androidx.lifecycle.LiveData
import com.example.janagroandroid.data.local.SessionManager
import com.example.janagroandroid.data.local.dao.CartDao
import com.example.janagroandroid.data.local.dao.HistoryDao
import com.example.janagroandroid.data.local.dao.ProductDao
import com.example.janagroandroid.data.local.dao.UserDao
import com.example.janagroandroid.data.local.entity.CartEntity
import com.example.janagroandroid.data.local.entity.HistoryEntity
import com.example.janagroandroid.data.local.entity.ProductEntity
import com.example.janagroandroid.data.local.entity.UserEntity
import com.example.janagroandroid.data.remote.ApiService
import com.example.janagroandroid.data.remote.dto.toEntity

class AppRepository(
    private val userDao: UserDao,
    private val productDao: ProductDao,
    private val cartDao: CartDao,
    private val historyDao: HistoryDao,
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {
    val products: LiveData<List<ProductEntity>> = productDao.getAll()
    val history: LiveData<List<HistoryEntity>> = historyDao.getAll()
    val getUser: LiveData<UserEntity?> = userDao.getCurrentUser()

    suspend fun getCurrentUser(): UserEntity? = userDao.getCurrentUserSync()

    fun currentUserId(): Long = getUser.value?.id ?: 0L
    fun isLoggedIn(): Boolean = getUser.value != null

    val cart: LiveData<List<CartEntity>>
        get() = cartDao.getByUser(currentUserId())

    val sellerProducts: LiveData<List<ProductEntity>>
        get() = productDao.getSellerProducts(currentUserId())

    suspend fun login(email: String, password: String): Boolean {
        return try {
            val response = apiService.login(mapOf("email" to email, "password" to password))
            if (response.isSuccessful) {
                val responseBody = response.body()
                val authData = responseBody?.data
                val userDto = authData?.user
                val token = authData?.token
                
                if (userDto != null && token != null) {
                    userDao.logoutAll()
                    val userEntity = userDto.toEntity(isLoggedIn = true)
                    userDao.insert(userEntity)
                    
                    // Simpan token ke SessionManager
                    sessionManager.saveToken(token)
                    
                    true
                } else {
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun register(user: UserEntity, passwordConfirm: String): Boolean {
        return try {
            val request = mapOf(
                "name" to user.name,
                "email" to user.email,
                "password" to user.password,
                "passwordConfirm" to passwordConfirm,
                "phone" to (user.phone ?: ""),
                "role" to user.role
            )
            val response = apiService.register(request)
            if (response.isSuccessful) {
                val responseBody = response.body()
                val authData = responseBody?.data
                
                // Cari UserDto baik di authData.user atau di root data (jika backend tidak membungkusnya)
                val userDto = authData?.user 
                
                if (userDto != null) {
                    userDao.insert(userDto.toEntity())
                    true
                } else {
                    responseBody?.status == "success"
                }
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun logout(): Boolean {
        return try {
            val response = apiService.logout()

            userDao.logoutAll()
            sessionManager.clear()
            response.isSuccessful
        } catch (e: Exception) {
            userDao.logoutAll()
            sessionManager.clear()
            false
        }
    }

    suspend fun addProduct(product: ProductEntity) {
        productDao.insert(product)
    }

    suspend fun refreshRemoteProducts(): Boolean {
        val response = apiService.getProducts()
        return if (response.isSuccessful) {
            val items = response.body()?.data?.products.orEmpty().map { it.toEntity(merchantId = 0) }
            productDao.insertAll(items)
            true
        } else {
            false
        }
    }

    suspend fun addToCart(item: CartEntity) {
        cartDao.insert(item)
    }

    suspend fun deleteCart(item: CartEntity) {
        cartDao.delete(item)
    }

    suspend fun deleteCartById(id: Long) {
        cartDao.deleteById(id)
    }

    suspend fun clearCart() {
        cartDao.clearByUser(currentUserId())
    }

    suspend fun checkout(total: Double) {
        historyDao.insert(
            HistoryEntity(
                userId = currentUserId(),
                date = System.currentTimeMillis().toString(),
                total = total,
                status = "PAID"
            )
        )
        clearCart()
    }
}