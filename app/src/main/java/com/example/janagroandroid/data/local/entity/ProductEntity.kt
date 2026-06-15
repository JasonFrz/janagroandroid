package com.example.janagroandroid.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val merchant_id: Long = 0,
    val merchant_name: String = "",
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val category: String = "",
    val imageUrl: String
)