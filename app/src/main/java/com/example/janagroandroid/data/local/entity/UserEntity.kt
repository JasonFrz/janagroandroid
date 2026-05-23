package com.example.janagroandroid.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val password: String = "",
    val phone: String? = null,
    val profilePicture: String? = null,
    val role: String = "Customer",
    val isMerchant: Boolean = false,
    val isLoggedIn: Boolean = false
)