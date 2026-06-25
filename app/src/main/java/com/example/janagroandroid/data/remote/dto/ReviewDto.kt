package com.example.janagroandroid.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReviewResponse(
    val status: String? = null,
    val message: String? = null,
    val data: ReviewData? = null
)

@JsonClass(generateAdapter = true)
data class ReviewData(
    val reviews: List<ReviewDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ReviewerDto(
    val id: Long = 0,
    val name: String = "User",
    @Json(name = "profile_picture")
    val profilePicture: String? = null
)

@JsonClass(generateAdapter = true)
data class ReviewDto(
    val id: Long,
    @Json(name = "user_id")
    val userId: Long,
    @Json(name = "product_id")
    val productId: Long,
    @Json(name = "order_id")
    val orderId: Long,
    val rating: Int,
    val comment: String?,
    @Json(name = "image_url")
    val imageUrl: String? = null,
    @Json(name = "created_at")
    val createdAt: String = "",
    @Json(name = "reviewer")
    val reviewer: ReviewerDto? = null
)

