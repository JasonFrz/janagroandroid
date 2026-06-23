package com.example.janagroandroid.data.remote

import com.example.janagroandroid.data.remote.dto.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("/api/v1/auth/login")
    suspend fun login(@Body request: Map<String, String>): Response<AuthResponse>

    @POST("/api/v1/auth/register")
    suspend fun register(@Body request: Map<String, String>): Response<AuthResponse>

    @POST("/api/v1/auth/logout")
    suspend fun logout(): Response<AuthResponse>

    @POST("/api/v1/auth/refresh-token")
    suspend fun refreshToken(@Body request: Map<String, String>): Response<AuthResponse>

//    PROFILE USERS API
    @GET("/api/v1/users/profile")
    suspend fun getProfile(): Response<AuthResponse>

    @Multipart
    @PATCH("/api/v1/users/profile")
    suspend fun updateProfile(
        @Part("name") name: RequestBody?,
        @Part("phone") phone: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Response<AuthResponse>

    @POST("/api/v1/merchants/request")
    suspend fun requestBecomeSeller(@Body request: Map<String, String>): Response<AuthResponse>

//    CART API
    @GET("/api/v1/cart")
    suspend fun getCart(): Response<CartResponse>

    @POST("/api/v1/cart")
    suspend fun addToCart(@Body request: Map<String, Long>): Response<CartResponse>

    @PATCH("/api/v1/cart/{id}")
    suspend fun updateCartItem(@Path("id") id: Long, @Body request: Map<String, Long>): Response<CartResponse>

    @DELETE("/api/v1/cart/{id}")
    suspend fun removeCartItem(@Path("id") id: Long): Response<CartResponse>

//    PRODUCT API
    @GET("/api/v1/products")
    suspend fun getProducts(
        @Query("search") search: String? = null,
        @Query("category_id") categoryId: Long? = null,
        @Query("merchant_id") merchantId: Long? = null,
        @Query("page") page: Int? = 1,
        @Query("limit") limit: Int? = 10,
        @Query("sortBy") sortBy: String? = null,
        @Query("sortDir") sortDir: String? = null
    ): Response<RemoteProductResponse>

    @GET("/api/v1/products/{id}")
    suspend fun getProductDetail(@Path("id") id: Long): Response<SingleProductResponse>

    @Multipart
    @POST("/api/v1/products")
    suspend fun createProduct(
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("price") price: RequestBody,
        @Part("stock") stock: RequestBody,
        @Part("category_id") categoryId: RequestBody,
        @Part images: List<MultipartBody.Part>
    ): Response<SingleProductResponse>

    @GET("/api/v1/products/{id}/reviews")
    suspend fun getProductReviews(@Path("id") id: Long): Response<ReviewResponse>

//    CATEGORY API
    @GET("/api/v1/categories")
    suspend fun getCategories(): Response<CategoryResponse>

//    MERCHANT API
    @GET("/api/v1/merchants/{id}")
    suspend fun getMerchantDetail(@Path("id") id: Long): Response<MerchantDetailResponse>

    @GET("/api/v1/merchants/highest-rated")
    suspend fun getHighestRatedMerchants(@Query("limit") limit: Int): Response<HighestRatedMerchantsResponse>

    @GET("/api/v1/merchants/pending")
    suspend fun getAllPendingMerchants(): Response<HighestRatedMerchantsResponse>

    @PATCH("/api/v1/merchants/{id}/status")
    suspend fun updateMerchantStatus(@Path("id") id: Long, @Body request: Map<String, String>): Response<AuthResponse>

//    ORDER API
    @GET("/api/v1/orders")
    suspend fun getOrders(): Response<OrderResponse>

    @GET("/api/v1/orders/{id}")
    suspend fun getOrderDetail(@Path("id") id: Long): Response<SingleOrderResponse>

//    ADMIN API
    @GET("/api/v1/admin/stats")
    suspend fun getAdminStats(): Response<AdminStatsResponse>

    @GET("/api/v1/admin/users")
    suspend fun getAdminUsers(
        @Query("search") search: String?,
        @Query("role") role: String?
    ): Response<AdminUsersResponse>

//    VOUCHER API
    @GET("/api/v1/vouchers")
    suspend fun getVouchers(): Response<VoucherListReponse>

    @POST("/api/v1/vouchers")
    suspend fun createVoucher(@Body request: VoucherRequest): Response<AuthResponse>

    @PATCH("/api/v1/vouchers/{id}")
    suspend fun updateVoucher(@Path("id") id: Long, @Body request: VoucherRequest): Response<AuthResponse>

    @DELETE("/api/v1/vouchers/{id}")
    suspend fun deleteVoucher(@Path("id") id: Long): Response<AuthResponse>

    // CHAT API
    @GET("/api/v1/chats/conversation/{partnerId}")
    suspend fun getConversation(@Path("partnerId") partnerId: Long): Response<ConversationResponse>
}
