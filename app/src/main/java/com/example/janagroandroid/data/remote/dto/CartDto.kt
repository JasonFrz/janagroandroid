package com.example.janagroandroid.data.remote.dto

import com.example.janagroandroid.data.local.entity.CartEntity
import com.google.gson.annotations.SerializedName

data class CartResponse(
    val status: String? = null,
    val message: String? = null,
    val data: CartData? = null
)

data class CartData(
    val cart: List<CartItemDto> = emptyList(),
    val item: CartItemDto? = null
)

data class CartItemDto(
    val id: Long,
    @SerializedName("user_id")
    val userId: Long,
    @SerializedName("product_id")
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
        price = product?.price ?: 0.0,
        imageUrl = product?.imageUrl ?: "",
        qty = quantity
    )
}
