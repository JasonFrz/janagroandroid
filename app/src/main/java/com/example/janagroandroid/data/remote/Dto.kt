package com.example.janagroandroid.data.remote

import com.example.janagroandroid.data.remote.dto.ProductDto

data class RemoteProductResponse(
    val status: String? = null,
    val message: String? = null,
    val data: RemoteProductData? = null
)

data class RemoteProductData(
    val total: Int = 0,
    val page: Int = 1,
    val limit: Int = 10,
    val totalPages: Int = 0,
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
