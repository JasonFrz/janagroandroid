package com.example.janagroandroid.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AdminStatsResponse(
    val status: String? = null,
    val message: String? = null,
    val data: AdminStatsData? = null
)

@JsonClass(generateAdapter = true)
data class AdminStatsData(
    val stats: AdminStats? = null
)

@JsonClass(generateAdapter = true)
data class AdminStats(
    val totalTransactionsToday: Long = 0,
    val activeUsers: Long = 0,
    val systemStatus: String = "-"
)

@JsonClass(generateAdapter = true)
data class AdminUsersResponse(
    val status: String? = null,
    val message: String? = null,
    val data: AdminUsersData? = null
)

@JsonClass(generateAdapter = true)
data class AdminUsersData(
    val users: List<AdminUserDto>? = null
)

@JsonClass(generateAdapter = true)
data class AdminUserDto(
    val id: Long = 0,
    val name: String = "-",
    val email: String = "-",
    val phone: String? = null,
    val role: String = "Customer",
    @Json(name = "profile_picture")
    val profilePicture: String? = null,
    @Json(name = "is_merchant")
    val isMerchant: Boolean = false,
    val status: String? = null,
    @Json(name = "created_at")
    val createdAt: String? = null
)