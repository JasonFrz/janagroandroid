package com.example.janagroandroid.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val productId: Int = 0,
    val sellerId: Int = 0,
    val name: String,
    val category: String,
    val price: Double,
    val stock: Int,
    val imageUrl: String = "",
    val description: String = ""
)