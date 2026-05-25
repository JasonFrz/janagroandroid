package com.example.janagroandroid.data.remote

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
