package com.example.janagroandroid.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotificationDto(
    val id: Long,
    @Json(name = "user_id")
    val userId: Long,
    val title: String,
    val message: String,
    val type: String,
    @Json(name = "is_read")
    val isRead: Boolean,
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "updated_at")
    val updatedAt: String
)

@JsonClass(generateAdapter = true)
data class NotificationResponse(
    val status: String? = null,
    val message: String? = null,
    val data: NotificationData? = null
)

@JsonClass(generateAdapter = true)
data class NotificationData(
    val notifications: List<NotificationDto> = emptyList()
)
