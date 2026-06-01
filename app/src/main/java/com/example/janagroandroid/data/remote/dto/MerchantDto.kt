package com.example.janagroandroid.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MerchantDto(
    val id: Long,
    @SerializedName("user_id")
    val userId: Long,
    @SerializedName("store_name")
    val storeName: String? = null,
    val description: String? = null,
    val address: String? = null,
    val status: String? = null,
    @SerializedName("average_rating")
    val averageRating: Double = 0.0,
    @SerializedName("review_count")
    val reviewCount: Int = 0,
    val owner: MerchantOwner? = null
)

data class MerchantOwner(
    val id: Long,
    val name: String,
    @SerializedName("profile_picture")
    val profilePicture: String? = null
)

data class HighestRatedMerchantsResponse(
    val status: String? = null,
    val message: String? = null,
    val data: HighestRatedMerchantsData? = null
)

data class HighestRatedMerchantsData(
    val merchants: List<MerchantDto> = emptyList()
)
