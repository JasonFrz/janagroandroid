package com.example.janagroandroid.data.remote

import com.example.janagroandroid.data.local.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:3000/"
//    private const val BASE_URL = "http://10.86.175.129:3000/"
    private var apiServiceInstance: ApiService? = null

    fun getApiService(sessionManager: SessionManager? = null): ApiService {
        if (apiServiceInstance != null) return apiServiceInstance!!

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
                val isAuthRoute = path.contains("/login") || path.contains("/register") || path.contains("/refresh-token")

                if (!isAuthRoute) {
                    sessionManager?.getToken()?.let { token ->
                        if (token.isNotEmpty()) {
                            requestBuilder.addHeader("Authorization", "Bearer $token")
                        }
                    }
                }

                val response = chain.proceed(requestBuilder.build())

                if (response.code == 401 && !isAuthRoute) {
                    synchronized(this) {
                        val refreshToken = sessionManager?.getRefreshToken()

                        if (refreshToken != null) {
                            // Try to refresh token
                            val refreshResponse = runBlocking {
                                // Create a separate instance for refresh to avoid interceptor loop
                                val refreshService = Retrofit.Builder()
                                    .baseUrl(BASE_URL)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build()
                                    .create(ApiService::class.java)

                                refreshService.refreshToken(mapOf("refreshToken" to refreshToken))
                            }

                            if (refreshResponse.isSuccessful) {
                                val newData = refreshResponse.body()?.data
                                val newToken = newData?.token
                                val newRefreshToken = newData?.refreshToken

                                if (newToken != null && newRefreshToken != null) {
                                    sessionManager.saveToken(newToken)
                                    sessionManager.saveRefreshToken(newRefreshToken)

                                    // Retry request with new token
                                    response.close() // Close the 401 response
                                    val newRequest = request.newBuilder()
                                        .header("Authorization", "Bearer $newToken")
                                        .build()
                                    return@Interceptor chain.proceed(newRequest)
                                }
                            }
                        }

                        // If refresh fails or no refresh token, clear session
                        sessionManager?.clear()
                        GlobalAuthHandler.logout()
                    }
                }

                response
            })
            .build()

        apiServiceInstance = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        return apiServiceInstance!!
    }

    val apiService: ApiService by lazy { getApiService() }
}