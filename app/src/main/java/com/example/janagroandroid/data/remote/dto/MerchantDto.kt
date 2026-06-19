package com.example.janagroandroid.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MerchantDto(
    val id: Long? = null,
    @Json(name = "user_id")
    val userId: Long? = null,
    @Json(name = "store_name")
    val storeName: String? = null,
    val description: String? = null,
    val city: String? = null,
    val profile_picture: String? = null,
    val status: String? = null,
    @Json(name = "created_at")
    val createdAt: String? = null,
    @Json(name = "updated_at")
    val updatedAt: String? = null,
    @Json(name = "average_rating")
    val averageRating: Double = 0.0,
    @Json(name = "review_count")
    val reviewCount: Int = 0,
    val owner: MerchantOwner? = null,
    val products: List<ProductDto>? = null
)

@JsonClass(generateAdapter = true)
data class MerchantOwner(
    val id: Long,
    val name: String,
    @Json(name = "profile_picture")
    val profilePicture: String? = null
)

@JsonClass(generateAdapter = true)
data class HighestRatedMerchantsResponse(
    val status: String? = null,
    val message: String? = null,
    val data: HighestRatedMerchantsData? = null
)

@JsonClass(generateAdapter = true)
data class HighestRatedMerchantsData(
    val merchants: List<MerchantDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class MerchantDetailResponse(
    val status: String? = null,
    val message: String? = null,
    val data: MerchantDetailData? = null
)

@JsonClass(generateAdapter = true)
data class MerchantDetailData(
    val merchant: MerchantDto? = null
)
