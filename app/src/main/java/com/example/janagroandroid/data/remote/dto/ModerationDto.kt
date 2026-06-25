package com.example.janagroandroid.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// ── Mini user used inside product/review moderation payloads ──────────────────
@JsonClass(generateAdapter = true)
data class AdminUserMini(
    val id: Long = 0,
    val name: String? = null,
    val email: String? = null
)

// ── Products moderation ───────────────────────────────────────────────────────
@JsonClass(generateAdapter = true)
data class AdminProductsResponse(
    val status: String? = null,
    val message: String? = null,
    val data: AdminProductsData? = null
)

@JsonClass(generateAdapter = true)
data class AdminProductsData(
    val products: List<AdminProductDto>? = null
)

@JsonClass(generateAdapter = true)
data class AdminProductDto(
    val id: Long = 0,
    val name: String = "-",
    val price: String? = null,
    val stock: Int = 0,
    val images: List<String>? = null,
    @Json(name = "created_at")
    val createdAt: String? = null,
    val merchant: AdminProductMerchant? = null,
    val category: AdminNamedRef? = null
)

@JsonClass(generateAdapter = true)
data class AdminProductMerchant(
    val id: Long = 0,
    @Json(name = "store_name")
    val storeName: String? = null,
    @Json(name = "user_id")
    val userId: Long? = null,
    val owner: AdminUserMini? = null
)

@JsonClass(generateAdapter = true)
data class AdminNamedRef(
    val id: Long = 0,
    val name: String? = null
)

// ── Reviews moderation ────────────────────────────────────────────────────────
@JsonClass(generateAdapter = true)
data class AdminReviewsResponse(
    val status: String? = null,
    val message: String? = null,
    val data: AdminReviewsData? = null
)

@JsonClass(generateAdapter = true)
data class AdminReviewsData(
    val reviews: List<AdminReviewDto>? = null
)

@JsonClass(generateAdapter = true)
data class AdminReviewDto(
    val id: Long = 0,
    val rating: Int = 0,
    val comment: String? = null,
    @Json(name = "is_hidden")
    val isHidden: Boolean = false,
    @Json(name = "created_at")
    val createdAt: String? = null,
    val reviewer: AdminUserMini? = null,
    val product: AdminNamedRef? = null
)
