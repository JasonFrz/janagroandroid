package com.example.janagroandroid.data.remote

import com.example.janagroandroid.data.remote.dto.AdminStats
import com.example.janagroandroid.data.remote.dto.AuthResponse
import com.example.janagroandroid.data.remote.dto.LoginRequest
import com.example.janagroandroid.data.remote.dto.RegisterRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    // Coba hapus prefix 'api/' jika backend Anda tidak menggunakannya
    // Jika backend Anda memamng menggunakan 'api/', pastikan routing di server sudah benar.
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(): Response<AuthResponse>

    @GET("auth/me")
    suspend fun getProfile(): Response<AuthResponse>

    @Multipart
    @PUT("auth/me")
    suspend fun updateProfile(
        @Part("name") name: RequestBody? = null,
        @Part("email") email: RequestBody? = null,
        @Part("phone") phone: RequestBody? = null,
        @Part profile_picture: MultipartBody.Part? = null
    ): Response<AuthResponse>

    @GET("products")
    suspend fun getProducts(): Response<RemoteProductResponse>

    @GET("products/{id}")
    suspend fun getProductDetail(@Path("id") id: Long): Response<SingleProductResponse>

    @GET("admin/stats")
    suspend fun getAdminStats(): Response<AdminStats>
}