package com.example.janagroandroid.data.remote

import com.example.janagroandroid.data.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Gunakan 10.0.2.2 untuk akses localhost dari emulator
    // Gunakan IP Laptop (misal 192.168.1.4) jika menggunakan HP Real
    private const val BASE_URL = "http://10.0.2.2:3000/"

    fun getApiService(sessionManager: SessionManager? = null): ApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(Interceptor { chain ->
                val request = chain.request()
                val path = request.url.encodedPath
                val isAuthRoute = path.contains("/api/auth/login") || path.contains("/api/auth/register")

                val builder = request.newBuilder()
                if (!isAuthRoute) {
                    val token = sessionManager?.getToken().orEmpty()
                    if (token.isNotBlank()) {
                        builder.addHeader("Authorization", "Bearer $token")
                    }
                }
                chain.proceed(builder.build())
            })
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}