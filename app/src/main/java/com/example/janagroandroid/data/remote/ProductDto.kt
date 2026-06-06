package com.example.janagroandroid.data.remote.dto

import com.example.janagroandroid.data.local.entity.ProductEntity
import com.google.gson.annotations.SerializedName

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
    @SerializedName("merchant_id")
    val merchantId: Long? = null,
    val name: String? = null,
    val price: String? = null,
    val stock: Int? = null,
    @SerializedName("category_id")
    val categoryId: Long? = null,
    val images: List<String>? = null,
    val description: String? = null,
    val category: CategoryDto? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
) {
    val priceDouble: Double get() = price?.toDoubleOrNull() ?: 0.0
    val firstImageUrl: String? get() = images?.firstOrNull()
}

fun ProductDto.toEntity(merchantId: Long = 0): ProductEntity {
    return ProductEntity(
        id = id ?: 0,
        merchant_id = this.merchantId ?: merchantId,
        name = name ?: "",
        price = priceDouble,
        stock = stock ?: 0,
        imageUrl = firstImageUrl ?: "",
        description = description ?: "",
        category = category?.name ?: ""
    )
}
