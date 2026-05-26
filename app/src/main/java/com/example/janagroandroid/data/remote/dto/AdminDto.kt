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
    val totalTransactionsToday: Long = 0,
    val activeUsers: Long = 0,
    val systemStatus: String = "-"
)