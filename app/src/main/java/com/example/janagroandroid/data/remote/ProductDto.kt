package com.example.janagroandroid.data.remote.dto

import com.example.janagroandroid.data.local.entity.ProductEntity

data class CategoryDto(
    val id: Long? = null,
    val name: String? = null
)

data class CategoryResponse(
    val status: String? = null,
    val message: String? = null,
    val data: CategoryData? = null
)

data class CategoryData(
    val categories: List<CategoryDto> = emptyList()
)

data class ProductDto(
    val id: Long? = null,
    val name: String? = null,
    val price: Double? = null,
    val stock: Int? = null,
    val imageUrl: String? = null,
    val description: String? = null,
    val category: CategoryDto? = null
)

fun ProductDto.toEntity(merchantId: Long = 0): ProductEntity {
    return ProductEntity(
        id = id ?: 0,
        merchant_id = merchantId,
        name = name ?: "",
        price = price ?: 0.0,
        stock = stock ?: 0,
        imageUrl = imageUrl ?: "",
        description = description ?: "",
        category = category?.name ?: ""
    )
}
