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
import retrofit2.Response

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

    val sellerProducts: LiveData<List<ProductEntity>> = getUser.switchMap { user ->
        productDao.getSellerProducts(user?.id ?: -1L)
    }

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
                    // Simpan user id + role agar chat bisa membedakan pesan sendiri vs lawan
                    sessionManager.saveUserId(userDto.id)
                    sessionManager.saveUserRole(userDto.role)

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

    suspend fun requestBecomeSeller(storeName: String, description: String): Boolean {
        return try {
            val request = mapOf(
                "store_name" to storeName,
                "description" to description
            )
            val response = apiService.requestBecomeSeller(request)
            if (response.isSuccessful) {
                refreshProfile()
                true
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

    suspend fun createRemoteProduct(
        name: String,
        description: String,
        price: Double,
        stock: Int,
        categoryId: Int,
        imageParts: List<MultipartBody.Part>
    ): Boolean {
        return try {
            val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val priceBody = price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val stockBody = stock.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val categoryIdBody = categoryId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val response = apiService.createProduct(
                nameBody,
                descriptionBody,
                priceBody,
                stockBody,
                categoryIdBody,
                imageParts
            )
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateRemoteProduct(
        productId: Long,
        name: String,
        description: String,
        price: Double,
        stock: Int,
        categoryId: Int,
        imageParts: List<MultipartBody.Part>
    ): Boolean {
        return try {
            val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val priceBody = price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val stockBody = stock.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val categoryIdBody = categoryId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val response = apiService.updateProduct(
                productId,
                nameBody,
                descriptionBody,
                priceBody,
                stockBody,
                categoryIdBody,
                imageParts
            )
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteRemoteProduct(productId: Long): Boolean {
        return try {
            apiService.deleteProduct(productId).isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun refreshRemoteProducts(): Boolean {
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

    suspend fun getProductsByMerchant(merchantId: Long): List<ProductDto> {
        return try {
            val response = apiService.getProducts(merchantId = merchantId, limit = 50)
            if (response.isSuccessful) {
                response.body()?.data?.products.orEmpty()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getCurrentSellerProducts(): List<ProductEntity> {
        return try {
            val currentUser = userDao.getCurrentUserSync()
            val response = apiService.getProducts(limit = 100)
            if (response.isSuccessful) {
                response.body()?.data?.products.orEmpty()
                    .filter { it.merchant?.userId == currentUser?.id }
                    .map { it.toEntity(merchantId = currentUser?.id ?: 0L) }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
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

    // ── Admin moderation ─────────────────────────────────────────────────────
    suspend fun setUserStatus(id: Long, status: String): Boolean {
        return try {
            apiService.updateUserStatus(id, mapOf("status" to status)).isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun adminDeleteUser(id: Long): Boolean {
        return try {
            apiService.adminDeleteUser(id).isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getAdminProducts(search: String? = null): List<AdminProductDto> {
        return try {
            val response = apiService.getAdminProducts(search)
            if (response.isSuccessful) {
                response.body()?.data?.products.orEmpty()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun adminDeleteProduct(id: Long): Boolean {
        return try {
            apiService.adminDeleteProduct(id).isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getAdminReviews(): List<AdminReviewDto> {
        return try {
            val response = apiService.getAdminReviews()
            if (response.isSuccessful) {
                response.body()?.data?.reviews.orEmpty()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun setReviewVisibility(id: Long, isHidden: Boolean): Boolean {
        return try {
            apiService.updateReviewVisibility(id, mapOf("is_hidden" to isHidden)).isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getVouchers(): List<VoucherDto>{
        return try {
            val response = apiService.getVouchers()
            if(response.isSuccessful){
                response.body()?.data.orEmpty()
            }else{
                emptyList()
            }
        } catch (e: Exception){
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getActiveVouchers(): List<VoucherDto>{
        return try {
            val response = apiService.getActiveVouchers()
            if(response.isSuccessful){
                response.body()?.data.orEmpty()
            }else{
                emptyList()
            }
        } catch (e: Exception){
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun createVouchers(request: VoucherRequest): Boolean {
        return try {
            apiService.createVoucher(request).isSuccessful
        }catch (e: Exception){
            e.printStackTrace()
            false
        }
    }

    suspend fun updateVouchers(id: Long, request: VoucherRequest): Boolean {
        return try {
            apiService.updateVoucher(id,request).isSuccessful
        }catch (e: Exception){
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteVouchers(id:Long): Boolean {
        return try {
            apiService.deleteVoucher(id).isSuccessful
        } catch (e: Exception){
            e.printStackTrace()
            false
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

    suspend fun getProductReviews(id: Long): List<ReviewDto> {
        return try {
            val response = apiService.getProductReviews(id)
            if (response.isSuccessful) {
                response.body()?.data?.reviews.orEmpty()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun submitReview(
        productId: Long,
        rating: Int,
        comment: String?,
        imagePart: MultipartBody.Part?
    ): Boolean {
        return try {
            val ratingBody = rating.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val commentBody = comment?.toRequestBody("text/plain".toMediaTypeOrNull())
            
            val response = apiService.submitReview(productId, ratingBody, commentBody, imagePart)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
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

    suspend fun getRemoteMerchantDetail(id: Long): MerchantDto? {
        return try {
            val response = apiService.getMerchantDetail(id)
            if (response.isSuccessful) {
                response.body()?.data?.merchant
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
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

    suspend fun getCartItemsByIds(ids: LongArray): List<CartEntity> {
        return cartDao.getItemsByIds(ids)
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

    suspend fun addRemoteCart(productId: Long, quantity: Int): Pair<Boolean, String?> {
        return try {
            val response = apiService.addToCart(mapOf("product_id" to productId, "quantity" to quantity.toLong()))
            if (response.isSuccessful) {
                getRemoteCart()
                Pair(true, null)
            } else {
                val errorMsg = try {
                    val errBody = response.errorBody()?.string()
                    // Try to extract message from JSON body e.g. {"status":"fail","message":"..."}
                    errBody?.let {
                        val msgStart = it.indexOf("\"message\":\"")
                        if (msgStart >= 0) {
                            val start = msgStart + 11
                            val end = it.indexOf("\"", start)
                            if (end > start) it.substring(start, end) else null
                        } else null
                    }
                } catch (e: Exception) { null }
                Pair(false, errorMsg)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(false, e.message)
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

    suspend fun removeCartItem(id: Long): Boolean {
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

    suspend fun removeAllRemoteCart(): Boolean {
        return try {
            val response = apiService.removeAllCartItems()
            if (response.isSuccessful) {
                val userId = currentUserId()
                if (userId != null) {
                    cartDao.clearByUser(userId)
                }
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
        val userId = currentUserId()
        if (userId != null) {
            cartDao.clearByUser(userId)
            removeAllRemoteCart()
        }
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

    suspend fun getConversations(): List<ChatRoomDto> {
        return try {
            val response = apiService.getConversations()
            if (response.isSuccessful) {
                response.body()?.data?.conversations.orEmpty().map { it.toRoom() }
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

    suspend fun payOrder(orderId: Long): Boolean {
        return try {
            val response = apiService.payOrder(orderId)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun completeOrder(orderId: Long): Boolean {
        return try {
            val response = apiService.completeOrder(orderId)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun buyAgain(orderId: Long): Boolean {
        return try {
            val order = getRemoteOrderDetail(orderId) ?: return false
            val items = order.items ?: return false

            var allSuccess = true
            for (item in items) {
                val (success, _) = addRemoteCart(item.productId, item.quantity)
                if (!success) allSuccess = false
            }
            allSuccess
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Checkout the user's server-side cart by calling the order API.
     * The backend computes totals from the cart, creates orders per merchant,
     * generates payment tokens, and clears the cart server-side.
     * Returns Pair(success, message) where message is the server message on success
     * or an error message on failure.
     */
    suspend fun checkout(
        shippingAddress: String,
        courier: String? = null,
        voucherCode: String? = null,
        paymentType: String? = null,
        cartItemIds: LongArray? = null
    ): Pair<Boolean, com.example.janagroandroid.data.remote.dto.OrderResponse?> {
        return try {
            val request = CheckoutRequest(
                shippingAddress = shippingAddress,
                courier = courier?.ifBlank { null },
                voucherCode = voucherCode?.ifBlank { null },
                paymentType = paymentType?.ifBlank { null },
                cartItemIds = cartItemIds?.toList()
            )
            val response = apiService.checkout(request)
            if (response.isSuccessful) {
                // Cart was cleared on the server; sync local cache to reflect it.
                getRemoteCart()
                Pair(true, response.body())
            } else {
                Pair(false, com.example.janagroandroid.data.remote.dto.OrderResponse(message = parseErrorMessage(response.errorBody()?.string())))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(false, com.example.janagroandroid.data.remote.dto.OrderResponse(message = e.message))
        }
    }

    /** Extract the "message" field from a JSON error body like {"status":"fail","message":"..."}. */
    private fun parseErrorMessage(body: String?): String? {
        body ?: return null
        return try {
            val key = "\"message\":\""
            val msgStart = body.indexOf(key)
            if (msgStart >= 0) {
                val start = msgStart + key.length
                val end = body.indexOf("\"", start)
                if (end > start) body.substring(start, end) else null
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getNotifications(): List<NotificationDto> {
        return try {
            val response = apiService.getNotifications()
            if (response.isSuccessful) {
                response.body()?.data?.notifications.orEmpty()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun readNotifications(): Boolean {
        return try {
            apiService.readNotifications().isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
