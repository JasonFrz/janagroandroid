package com.example.janagroandroid.data.remote

import com.example.janagroandroid.data.remote.dto.AdminStatsResponse
import com.example.janagroandroid.data.remote.dto.AuthResponse
import com.example.janagroandroid.data.remote.dto.HighestRatedMerchantsResponse
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

    @POST("/api/v1/auth/refresh")
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
}
