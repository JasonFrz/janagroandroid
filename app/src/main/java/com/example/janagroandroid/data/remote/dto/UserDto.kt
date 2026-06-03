package com.example.janagroandroid.data.remote.dto

import com.example.janagroandroid.data.local.entity.UserEntity
import com.google.gson.annotations.SerializedName

data class UserDto(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String? = null,
    @SerializedName("profile_picture")
    val profilePicture: String? = null,
    val role: String,
    @SerializedName("is_merchant")
    val isMerchant: Boolean = false
)

data class AuthResponse(
    val status: String? = null,
    val message: String? = null,
    val data: AuthData? = null
)

data class AuthData(
    val token: String? = null,
    @SerializedName("refreshToken")
    val refreshToken: String? = null,
    val user: UserDto? = null
)

fun UserDto.toEntity(isLoggedIn: Boolean = false): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        email = email,
        phone = phone,
        profilePicture = profilePicture,
        role = role,
        isMerchant = isMerchant,
        isLoggedIn = isLoggedIn
    )
}