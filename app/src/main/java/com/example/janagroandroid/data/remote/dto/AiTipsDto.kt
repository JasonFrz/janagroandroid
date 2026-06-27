package com.example.janagroandroid.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    @Json(name = "statusCode") val statusCode: Int? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: T? = null
)

@JsonClass(generateAdapter = true)
data class ChatPartnerDto(
    @Json(name = "partner") val partner: ChatPartnerDetail? = null
)

@JsonClass(generateAdapter = true)
data class ChatPartnerDetail(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "image") val image: String? = null
)

@JsonClass(generateAdapter = true)
data class AiTipsDto(
    @Json(name = "tips") val tips: String
)
