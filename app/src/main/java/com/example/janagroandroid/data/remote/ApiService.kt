package com.example.janagroandroid.data.remote

import com.example.janagroandroid.data.remote.dto.AuthResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("/api/v1/products")
    suspend fun getProducts(): Response<RemoteProductResponse>

    @POST("/api/v1/auth/login")
    suspend fun login(@Body request: Map<String, String>): Response<AuthResponse>

    @POST("/api/v1/register")
    suspend fun register(@Body request: Map<String, Any>): Response<AuthResponse>

    @GET("/api/v1/profile")
    suspend fun getProfile(): Response<AuthResponse>

    @Multipart
    @PUT("/api/v1/profile")
    suspend fun updateProfile(
        @Part("name") name: RequestBody?,
        @Part("phone") phone: RequestBody?,
        @Part profile_picture: MultipartBody.Part?
    ): Response<AuthResponse>
}