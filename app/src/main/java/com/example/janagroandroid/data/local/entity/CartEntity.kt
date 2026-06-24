package com.example.janagroandroid.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val productId: Long,
    val productName: String,
    val price: Double,
    val imageUrl: String,
    val qty: Int,
    val merchantId: Long = 0L,
    val merchantName: String = ""
)