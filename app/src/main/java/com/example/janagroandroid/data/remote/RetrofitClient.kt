package com.example.janagroandroid.data.remote

import com.example.janagroandroid.data.local.SessionManager
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:3000/"

    fun getApiService(sessionManager: SessionManager? = null): ApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(Interceptor { chain ->
                val request = chain.request()
                val requestBuilder = request.newBuilder()
                
                // Jangan kirim token untuk endpoint login dan register
                val path = request.url.encodedPath
                val isAuthRoute = path.contains("/login") || path.contains("/register")

                if (!isAuthRoute) {
                    sessionManager?.getToken()?.let { token ->
                        if (token.isNotEmpty()) {
                            requestBuilder.addHeader("Authorization", "Bearer $token")
                        }
                    }
                }
                
                chain.proceed(requestBuilder.build())
            })
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    val apiService: ApiService by lazy { getApiService() }
}