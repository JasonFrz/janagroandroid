package com.example.janagroandroid.data.remote.dto

data class AdminStatsResponse(
    val status: String? = null,
    val message: String? = null,
    val data: AdminStatsData? = null
)

data class AdminStatsData(
    val stats: AdminStats? = null
)

data class AdminStats(
    val totalUsers: Int = 0,
    val totalProducts: Int = 0,
    val totalTransactions: Int = 0
)