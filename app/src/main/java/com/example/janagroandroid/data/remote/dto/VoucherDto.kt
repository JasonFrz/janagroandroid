package com.example.janagroandroid.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VoucherDto(
    val id: Long = 0,
    val code: String = "",
    val description: String? = null,
    @Json(name = "discount_type")
    val discountType: String = "Fixed_Amount",
    @Json(name = "discount_value")
    val discountValue: String = "0",
    @Json(name = "min_purchase")
    val minPurchase: String = "0",
    @Json(name = "max_discount")
    val maxDiscount: String? = null,
    val quota: Int = 0,
    @Json(name = "max_uses_per_user")
    val maxUsesPerUser: Int = 1,
    @Json(name = "start_date")
    val startDate: String? = null,
    @Json(name = "end_date")
    val endDate: String? = null,
    @Json(name = "is_active")
    val isActive: Boolean = true,
    @Json(name = "created_at")
    val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class VoucherListReponse(
    val status: String? = null,
    val message: String? = null,
    val data: VoucherListData? = null
)

@JsonClass(generateAdapter = true)
data class VoucherListData(
    val vouchers: List<VoucherDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class VoucherRequest(
    val code: String,
    val description: String?,
    @Json(name = "discount_type")
    val discountType: String,
    @Json(name = "discount_value")
    val discountValue: String,
    @Json(name = "min_purchase")
    val minPurchase: String,
    @Json(name = "max_discount")
    val maxDiscount: String?,
    val quota: Int,
    @Json(name = "max_uses_per_user")
    val maxUsesPerUser: Int,
    @Json(name = "start_date")
    val startDate: String,
    @Json(name = "end_date")
    val endDate: String,
    @Json(name = "is_active")
    val isActive: Boolean
)


