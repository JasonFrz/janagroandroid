package com.example.janagroandroid.data.remote.dto

import com.example.janagroandroid.data.local.entity.UserEntity

data class UserDto(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String
)

data class AuthData(
    val token: String? = null,
    val user: UserDto? = null
)

data class AuthResponse(
    val message: String? = null,
    val data: AuthData? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val phone: String,
    val password: String
)

fun UserDto.toEntity(
    password: String = "",
    isLoggedIn: Boolean = false
): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        email = email,
        phone = phone,
        password = password,
        isLoggedIn = isLoggedIn
    )
}