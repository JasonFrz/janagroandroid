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
    @PUT("/api/v1/users/profile")
    suspend fun updateProfile(
        @Part("name") name: RequestBody?,
        @Part("phone") phone: RequestBody?,
        @Part("oldPassword") oldPassword: RequestBody?,
        @Part("newPassword") newPassword: RequestBody?,
        @Part("confirmNewPassword") confirmNewPassword: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Response<AuthResponse>

    @POST("/api/v1/merchants/apply")
    suspend fun requestBecomeSeller(@Body request: Map<String, String>): Response<AuthResponse>

//    CART API
    @GET("/api/v1/cart")
    suspend fun getCart(): Response<CartResponse>

    @POST("/api/v1/cart")
    suspend fun addToCart(@Body request: Map<String, Long>): Response<CartResponse>

    @PUT("/api/v1/cart/{id}")
    suspend fun updateCartItem(@Path("id") id: Long, @Body request: Map<String, Long>): Response<CartResponse>

    @DELETE("/api/v1/cart/{id}")
    suspend fun removeCartItem(@Path("id") id: Long): Response<CartResponse>

    @DELETE("/api/v1/cart")
    suspend fun removeAllCartItems(): Response<CartResponse>

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
    @POST("/api/v1/merchant/products")
    suspend fun createProduct(
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("price") price: RequestBody,
        @Part("stock") stock: RequestBody,
        @Part("category_id") categoryId: RequestBody,
        @Part images: List<MultipartBody.Part>
    ): Response<SingleProductResponse>

    @Multipart
    @PUT("/api/v1/merchant/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Long,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("price") price: RequestBody,
        @Part("stock") stock: RequestBody,
        @Part("category_id") categoryId: RequestBody,
        @Part images: List<MultipartBody.Part>
    ): Response<SingleProductResponse>

    @DELETE("/api/v1/merchant/products/{id}")
    suspend fun deleteProduct(@Path("id") id: Long): Response<Void>

    @GET("/api/v1/products/{id}/chat-partner")
    suspend fun getProductChatPartner(
        @Path("id") id: Long
    ): Response<ApiResponse<ChatPartnerDto>>

    @GET("/api/v1/products/{id}/ai-tips")
    suspend fun getProductAiTips(
        @Path("id") id: Long
    ): Response<ApiResponse<AiTipsDto>>

    @GET("/api/v1/products/{id}/reviews")
    suspend fun getProductReviews(@Path("id") id: Long): Response<ReviewResponse>

    @Multipart
    @POST("/api/v1/products/{id}/reviews")
    suspend fun submitReview(
        @Path("id") id: Long,
        @Part("rating") rating: RequestBody,
        @Part("comment") comment: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Response<ReviewResponse>

//    CATEGORY API
    @GET("/api/v1/categories")
    suspend fun getCategories(): Response<CategoryResponse>

//    MERCHANT API
    @GET("/api/v1/merchants/{id}")
    suspend fun getMerchantDetail(@Path("id") id: Long): Response<MerchantDetailResponse>

    @GET("/api/v1/merchants/highest-rated")
    suspend fun getHighestRatedMerchants(@Query("limit") limit: Int): Response<HighestRatedMerchantsResponse>

    @GET("/api/v1/admin/merchants/pending")
    suspend fun getAllPendingMerchants(): Response<HighestRatedMerchantsResponse>

    @PATCH("/api/v1/admin/merchants/{id}/status")
    suspend fun updateMerchantStatus(@Path("id") id: Long, @Body request: Map<String, String>): Response<AuthResponse>

//    ORDER API
    @POST("/api/v1/orders/checkout")
    suspend fun checkout(@Body request: CheckoutRequest): Response<OrderResponse>

    @POST("/api/v1/orders/{id}/pay")
    suspend fun payOrder(@Path("id") id: Long): Response<OrderResponse>

    @PUT("/api/v1/orders/{id}/complete")
    suspend fun completeOrder(@Path("id") id: Long): Response<OrderResponse>

    @GET("/api/v1/orders")
    suspend fun getOrders(): Response<OrderResponse>

    @GET("/api/v1/orders/{id}")
    suspend fun getOrderDetail(@Path("id") id: Long): Response<SingleOrderResponse>

    // SELLER ORDER (Manage Transactions)
    @GET("/api/v1/merchant/orders")
    suspend fun getMerchantOrders(): Response<OrderResponse>

    @PATCH("/api/v1/merchant/orders/{id}/status")
    suspend fun updateOrderShippingStatus(
        @Path("id") id: Long,
        @Body request: Map<String, String>
    ): Response<SingleOrderResponse>

    // VOUCHER API
    @GET("/api/v1/vouchers/active")
    suspend fun getActiveVouchers(): Response<VoucherListReponse>

//    ADMIN API
    @GET("/api/v1/admin/stats")
    suspend fun getAdminStats(): Response<AdminStatsResponse>

    @GET("/api/v1/admin/users")
    suspend fun getAdminUsers(
        @Query("search") search: String?,
        @Query("role") role: String?
    ): Response<AdminUsersResponse>

//    ADMIN MODERATION API
    @PATCH("/api/v1/admin/users/{id}/status")
    suspend fun updateUserStatus(
        @Path("id") id: Long,
        @Body request: Map<String, String>
    ): Response<AuthResponse>

    @DELETE("/api/v1/admin/users/{id}")
    suspend fun adminDeleteUser(@Path("id") id: Long): Response<AuthResponse>

    @GET("/api/v1/admin/products")
    suspend fun getAdminProducts(@Query("search") search: String?): Response<AdminProductsResponse>

    @DELETE("/api/v1/admin/products/{id}")
    suspend fun adminDeleteProduct(@Path("id") id: Long): Response<AuthResponse>

    @GET("/api/v1/admin/reviews")
    suspend fun getAdminReviews(): Response<AdminReviewsResponse>

    @PATCH("/api/v1/admin/reviews/{id}/visibility")
    suspend fun updateReviewVisibility(
        @Path("id") id: Long,
        @Body request: Map<String, Boolean>
    ): Response<AuthResponse>

//    VOUCHER API
    @GET("/api/v1/admin/vouchers")
    suspend fun getVouchers(): Response<VoucherListReponse>

    @POST("/api/v1/admin/vouchers")
    suspend fun createVoucher(@Body request: VoucherRequest): Response<AuthResponse>

    @PUT("/api/v1/admin/vouchers/{id}")
    suspend fun updateVoucher(@Path("id") id: Long, @Body request: VoucherRequest): Response<AuthResponse>

    @DELETE("/api/v1/admin/vouchers/{id}")
    suspend fun deleteVoucher(@Path("id") id: Long): Response<AuthResponse>

    // CHAT API
    @GET("/api/v1/chats/conversations")
    suspend fun getConversations(): Response<ConversationListResponse>

    @GET("/api/v1/chats/conversation/{partnerId}")
    suspend fun getConversation(@Path("partnerId") partnerId: Long): Response<ConversationResponse>

    // NOTIFICATION API
    @GET("/api/v1/notifications")
    suspend fun getNotifications(): Response<NotificationResponse>

    @PUT("/api/v1/notifications/read")
    suspend fun readNotifications(): Response<AuthResponse>
}
