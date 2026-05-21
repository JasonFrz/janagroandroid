package com.example.janagroandroid.data.remote

import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("/api/v1/products")
    suspend fun getProducts(): Response<RemoteProductResponse>

//    @GET("")
}