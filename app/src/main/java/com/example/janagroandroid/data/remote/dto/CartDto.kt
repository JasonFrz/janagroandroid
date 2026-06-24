package com.example.janagroandroid.data.remote.dto

import com.example.janagroandroid.data.local.entity.CartEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CartResponse(
    val status: String? = null,
    val message: String? = null,
    val data: CartData? = null
)

@JsonClass(generateAdapter = true)
data class CartData(
    val cart: List<CartItemDto> = emptyList(),
    val item: CartItemDto? = null
)

@JsonClass(generateAdapter = true)
data class CartItemDto(
    val id: Long,
    @Json(name = "user_id")
    val userId: Long,
    @Json(name = "product_id")
    val productId: Long,
    val quantity: Int,
    val product: ProductDto? = null
)

fun CartItemDto.toEntity(): CartEntity {
    return CartEntity(
        id = id,
        userId = userId,
        productId = productId,
        productName = product?.name ?: "Unknown",
        price = product?.priceDouble ?: 0.0,
        imageUrl = product?.firstImageUrl ?: "",
        qty = quantity,
        merchantId = product?.merchant?.id ?: 0L,
        merchantName = product?.merchant?.storeName ?: ""
    )
}
