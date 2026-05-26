package com.example.janagroandroid.data.remote

import com.example.janagroandroid.data.remote.dto.AdminStatsResponse
import com.example.janagroandroid.data.remote.dto.AuthResponse
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
    suspend fun getProducts(): Response<RemoteProductResponse>

    @GET("/api/v1/products/{id}")
    suspend fun getProductDetail(@Path("id") id: Long): Response<SingleProductResponse>

//    ADMIN API
    @GET("/api/v1/admin/stats")
    suspend fun getAdminStats(): Response<AdminStatsResponse>
}
