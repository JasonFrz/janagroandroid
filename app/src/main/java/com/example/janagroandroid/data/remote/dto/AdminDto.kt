package com.example.janagroandroid.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AdminStatsResponse(
    val status: String? = null,
    val message: String? = null,
    val data: AdminStatsData? = null
)

data class AdminStatsData(
    val stats: AdminStats? = null
)

data class AdminStats(
    val totalTransactionsToday: Long = 0,
    val activeUsers: Long = 0,
    val systemStatus: String = "-"
)

data class AdminUsersResponse(
    val status: String? = null,
    val message: String? = null,
    val data: AdminUsersData? = null
)

data class AdminUsersData(
    val users: List<AdminUserDto>? = null
)

data class AdminUserDto(
    val id: Long = 0,
    val name: String = "-",
    val email: String = "-",
    val phone: String? = null,
    val role: String = "Customer",
    @SerializedName("profile_picture")
    val profilePicture: String? = null,
    @SerializedName("is_merchant")
    val isMerchant: Boolean = false,
    val status: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null
)