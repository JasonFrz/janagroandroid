package com.example.janagroandroid.data.remote

import com.example.janagroandroid.data.remote.dto.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

//    AUTH API
    @POST("/api/v1/auth/login")
    suspend fun login(@Body request: Map<String, String>): Response<AuthResponse>

    @POST("/api/v1/auth/register")
    suspend fun register(@Body request: Map<String, String>): Response<AuthResponse>

    @POST("/api/v1/auth/logout")
    suspend fun logout(): Response<AuthResponse>

    @POST("/api/v1/auth/refresh-token")
    suspend fun refreshToken(@Body request: Map<String, String>): Response<AuthResponse>

//    PROFILE USERS API
    @GET("/api/v1/profile")
    suspend fun getProfile(): Response<AuthResponse>

    @Multipart
    @PUT("/api/v1/profile")
    suspend fun updateProfile(
        @Part("name") name: RequestBody?,
        @Part("phone") phone: RequestBody?,
        @Part profile_picture: MultipartBody.Part?
    ): Response<AuthResponse>

//    CART API
    @GET("/api/v1/cart")
    suspend fun getCart(): Response<CartResponse>

    @POST("/api/v1/cart")
    suspend fun addToCart(@Body request: Map<String, Long>): Response<CartResponse>

    @PUT("/api/v1/cart/{id}")
    suspend fun updateCartItem(@Path("id") id: Long, @Body request: Map<String, Long>): Response<CartResponse>

    @DELETE("/api/v1/cart/{id}")
    suspend fun removeCartItem(@Path("id") id: Long): Response<CartResponse>

//    PRODUCT API
    @GET("/api/v1/products")
    suspend fun getProducts(
        @Query("search") search: String? = null,
        @Query("category_id") categoryId: Int? = null,
        @Query("minPrice") minPrice: Double? = null,
        @Query("maxPrice") maxPrice: Double? = null,
        @Query("page") page: Int? = 1,
        @Query("limit") limit: Int? = 10,
        @Query("sortBy") sortBy: String? = "created_at",
        @Query("sortDir") sortDir: String? = "DESC"
    ): Response<RemoteProductResponse>

    @GET("/api/v1/products/{id}")
    suspend fun getProductDetail(@Path("id") id: Long): Response<SingleProductResponse>

//    MERCHANT API
    @GET("/api/v1/merchants/highest-rated")
    suspend fun getHighestRatedMerchants(@Query("limit") limit: Int = 6): Response<HighestRatedMerchantsResponse>

//    ADMIN API
    @GET("/api/v1/admin/stats")
    suspend fun getAdminStats(): Response<AdminStatsResponse>

    @GET("/api/v1/admin/users")
    suspend fun getAdminUsers(
        @Query("search") search: String? = null,
        @Query("role") role: String? = null
    ): Response<AdminUsersResponse>

    @GET("/api/v1/admin/merchants/pending")
    suspend fun getAllPendingMerchants(): Response<HighestRatedMerchantsResponse>
    @PATCH("/api/v1/admin/merchants/{id}/status")
    suspend fun updateMerchantStatus(
        @Path("id") id: Long,
        @Body request: Map<String, String>
    ): Response<Unit>

}
