package com.example.janagroandroid.data.remote.dto

import com.example.janagroandroid.data.local.entity.ProductEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ActiveNegotiationResponse(
    val status: String? = null,
    val message: String? = null,
    val data: ActiveNegotiationData? = null
)

@JsonClass(generateAdapter = true)
data class ActiveNegotiationData(
    val active: Boolean = false,
    @Json(name = "negotiated_price")
    val negotiatedPrice: Double? = null,
    @Json(name = "remaining_minutes")
    val remainingMinutes: Int? = null,
    @Json(name = "expires_at")
    val expiresAt: String? = null
)

@JsonClass(generateAdapter = true)
data class CategoryDto(
    val id: Long? = null,
    val name: String? = null
)

@JsonClass(generateAdapter = true)
data class CategoryResponse(
    val status: String? = null,
    val message: String? = null,
    val data: CategoryData? = null
)

@JsonClass(generateAdapter = true)
data class CategoryData(
    val categories: List<CategoryDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ProductDto(
    val id: Long? = null,
    @Json(name = "merchant_id")
    val merchantId: Long? = null,
    val name: String? = null,
    val price: String? = null,
    val stock: Int? = null,
    @Json(name = "category_id")
    val categoryId: Long? = null,
    val images: List<String> = emptyList(),
    val description: String? = null,
    val category: CategoryDto? = null,
    val merchant: MerchantDto? = null,
    @Json(name = "created_at")
    val createdAt: String? = null,
    @Json(name = "updated_at")
    val updatedAt: String? = null,
    @Json(name = "wholesale_min_qty")
    val wholesaleMinQty: Int? = null,
    @Json(name = "wholesale_price")
    val wholesalePrice: String? = null
) {
    val priceDouble: Double get() = price?.toDoubleOrNull() ?: 0.0
    val wholesalePriceDouble: Double? get() = wholesalePrice?.toDoubleOrNull()
    val firstImageUrl: String? get() {
        val url = images.firstOrNull()
        return url?.replace("http://", "https://")
    }
}

fun ProductDto.toEntity(merchantId: Long = 0): ProductEntity {
    return ProductEntity(
        id = id ?: 0,
        merchant_id = this.merchantId ?: merchant?.id ?: merchantId,
        merchantUserId = merchant?.userId ?: merchant?.owner?.id ?: 0L,
        merchant_name = merchant?.storeName ?: merchant?.owner?.name ?: "",
        merchant_city = merchant?.city ?: "",
        name = name ?: "",
        price = priceDouble,
        stock = stock ?: 0,
        imageUrl = firstImageUrl ?: "",
        description = description ?: "",
        category = category?.name ?: "",
        wholesaleMinQty = wholesaleMinQty,
        wholesalePrice = wholesalePriceDouble,
        merchantProfileUrl = merchant?.profile_picture ?: merchant?.owner?.profilePicture ?: "",
        createdAt = createdAt ?: ""
    )
}
