package com.example.janagroandroid.data.remote

import com.example.janagroandroid.data.remote.dto.ProductDto

data class RemoteProductResponse(
    val success: Boolean,
    val message: String? = null,
    val data: RemoteProductData? = null
)

data class RemoteProductData(
    val products: List<ProductDto> = emptyList()
)
