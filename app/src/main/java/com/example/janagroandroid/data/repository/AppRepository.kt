package com.example.janagroandroid.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
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
import com.example.janagroandroid.data.remote.dto.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

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

    val cart: LiveData<List<CartEntity>> = getUser.switchMap { user ->
        cartDao.getByUser(user?.id ?: -1L)
    }

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
                    authData.refreshToken?.let { sessionManager.saveRefreshToken(it) }

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

    suspend fun refreshProfile(): Boolean {
        return try {
            val response = apiService.getProfile()
            if (response.isSuccessful) {
                val userDto = response.body()?.data?.user
                if (userDto != null) {
                    userDao.insert(userDto.toEntity(isLoggedIn = true))
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

    suspend fun updateProfile(
        name: String? = null,
        phone: String? = null,
        imagePart: MultipartBody.Part? = null
    ): Boolean {
        return try {
            val nameBody = name?.toRequestBody("text/plain".toMediaTypeOrNull())
            val phoneBody = phone?.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = apiService.updateProfile(nameBody, phoneBody, imagePart)
            if (response.isSuccessful) {
                val updatedUser = response.body()?.data?.user
                if (updatedUser != null) {
                    userDao.insert(updatedUser.toEntity(isLoggedIn = true))
                    true
                } else {
                    response.body()?.status == "success"
                }
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun addProduct(product: ProductEntity) {
        productDao.insert(product)
    }

    suspend fun refreshRemoteProducts(): Boolean {
        // Menggunakan parameter baru: limit=10, sortBy=created_at, sortDir=DESC
        val response = apiService.getProducts(limit = 10, sortBy = "created_at", sortDir = "DESC")
        return if (response.isSuccessful) {
            val items = response.body()?.data?.products.orEmpty()
                .map { it.toEntity(merchantId = 0) }
            productDao.insertAll(items)
            true
        } else {
            false
        }
    }

    suspend fun getAdminStats(): AdminStats? {
        return try {
            val response = apiService.getAdminStats()
            if (response.isSuccessful) {
                response.body()?.data?.stats
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getAdminUsers(search: String? = null, role: String? = null): List<AdminUserDto> {
        return try {
            val response = apiService.getAdminUsers(search, role)
            if (response.isSuccessful) {
                response.body()?.data?.users.orEmpty()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getRemoteProductDetail(id: Long): ProductEntity? {
        return try {
            val response = apiService.getProductDetail(id)
            if (response.isSuccessful) {
                val productDto = response.body()?.data?.product
                productDto?.toEntity()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getHighestRatedMerchants(limit: Int = 6): List<MerchantDto> {
        return try {
            val response = apiService.getHighestRatedMerchants(limit)
            if (response.isSuccessful) {
                response.body()?.data?.merchants.orEmpty()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getPendingMerchants(): List<MerchantDto> {
        return try{
            val response = apiService.getAllPendingMerchants()
            if(response.isSuccessful){
                response.body()?.data?.merchants.orEmpty()
            } else{
                emptyList()
            }
        } catch (e: Exception){
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun updateMerchantStatus(id: Long, status: String): Boolean {
        return try {
            val response = apiService.updateMerchantStatus(id, mapOf("status" to status))
            response.isSuccessful
        }catch (e: Exception){
            e.printStackTrace()
            false
        }
    }

    suspend fun addToCart(item: CartEntity) {
        cartDao.insert(item)
    }

    suspend fun getRemoteCart(): Boolean {
        return try {
            val response = apiService.getCart()
            if (response.isSuccessful) {
                val cartItems = response.body()?.data?.cart.orEmpty()
                val entities = cartItems.map { it.toEntity() }
                
                val userId = userDao.getCurrentUserId()
                if (userId != null) {
                    cartDao.clearByUser(userId)
                    entities.forEach { cartDao.insert(it) }
                }
                true
            } else false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun addRemoteCart(productId: Long, quantity: Int): Boolean {
        return try {
            val response = apiService.addToCart(mapOf("product_id" to productId, "quantity" to quantity.toLong()))
            if (response.isSuccessful) {
                getRemoteCart()
                true
            } else false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateRemoteCart(id: Long, quantity: Int): Boolean {
        return try {
            val response = apiService.updateCartItem(id, mapOf("quantity" to quantity.toLong()))
            if (response.isSuccessful) {
                getRemoteCart()
                true
            } else false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun removeRemoteCart(id: Long): Boolean {
        return try {
            val response = apiService.removeCartItem(id)
            if (response.isSuccessful) {
                cartDao.deleteById(id)
                true
            } else false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
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

    suspend fun getCategories(): List<CategoryDto> {
        return try {
            val response = apiService.getCategories()
            if (response.isSuccessful) {
                response.body()?.data?.categories.orEmpty()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getRemoteOrders(): List<OrderDto> {
        return try {
            val response = apiService.getOrders()
            if (response.isSuccessful) {
                response.body()?.data?.orders.orEmpty()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getRemoteOrderDetail(id: Long): OrderDto? {
        return try {
            val response = apiService.getOrderDetail(id)
            if (response.isSuccessful) {
                response.body()?.data?.order
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun buyAgain(orderId: Long): Boolean {
        return try {
            val order = getRemoteOrderDetail(orderId) ?: return false
            val items = order.items ?: return false
            
            var allSuccess = true
            for (item in items) {
                val success = addRemoteCart(item.productId, item.quantity)
                if (!success) allSuccess = false
            }
            allSuccess
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
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
