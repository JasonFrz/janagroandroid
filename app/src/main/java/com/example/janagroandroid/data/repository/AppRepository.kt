package com.example.janagroandroid.data.repository

import androidx.lifecycle.LiveData
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
    private val apiService: ApiService
) {
    val products: LiveData<List<ProductEntity>> = productDao.getAll()
    val history: LiveData<List<HistoryEntity>> = historyDao.getAll()
    val getUser: LiveData<UserEntity?> = userDao.getCurrentUser()

    fun currentUserId(): Long = getUser.value?.id ?: 0L
    fun isLoggedIn(): Boolean = getUser.value != null

    val cart: LiveData<List<CartEntity>>
        get() = cartDao.getByUser(currentUserId())

    val sellerProducts: LiveData<List<ProductEntity>>
        get() = productDao.getSellerProducts(currentUserId())

    suspend fun login(email: String, password: String): Boolean {
        val user = userDao.login(email, password)
        return if (user != null) {
            userDao.logoutAll()
            userDao.setLoggedIn(user.id)
            true
        } else {
            false
        }
    }

    suspend fun register(user: UserEntity): Boolean {
        val exists = userDao.findByEmail(user.email)
        return if (exists == null) {
            userDao.insert(user)
            true
        } else {
            false
        }
    }

    suspend fun logout() {
        userDao.logoutAll()
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