package com.example.janagroandroid.data.local.entity


data class ProductEntity(
    val id: Long = 0,
    val merchant_id: Long = 0,
    val merchantUserId: Long = 0,
    val merchant_name: String = "",
    val merchant_city: String = "",
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val category: String = "",
    val imageUrl: String,
    val createdAt: String = ""
)