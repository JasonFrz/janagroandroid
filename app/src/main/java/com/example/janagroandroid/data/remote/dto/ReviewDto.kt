package com.example.janagroandroid.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ReviewResponse(
    val status: String? = null,
    val message: String? = null,
    val data: ReviewData? = null
)

data class ReviewData(
    val reviews: List<ReviewDto> = emptyList()
)

data class ReviewDto(
    val id: Long,
    @SerializedName("user_id")
    val userId: Long,
    @SerializedName("product_id")
    val productId: Long,
    @SerializedName("order_id")
    val orderId: Long,
    val rating: Int,
    val comment: String?,
    @SerializedName("created_at")
    val createdAt: String,
    val user: UserDto? = null
)
