package com.example.janagroandroid.data.remote

import com.example.janagroandroid.data.remote.dto.ProductDto

data class RemoteProductResponse(
    val status: String? = null,
    val message: String? = null,
    val data: RemoteProductData? = null
)

data class RemoteProductData(
    val products: List<ProductDto> = emptyList()
)

data class SingleProductResponse(
    val status: String? = null,
    val message: String? = null,
    val data: SingleProductData? = null
)

data class SingleProductData(
    val product: ProductDto? = null
)
