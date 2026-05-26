package com.example.janagroandroid.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val userId: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val role: String
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val productId: Int = 0,
    val sellerId: Int = 0,
    val name: String,
    val category: String,
    val price: Double,
    val stock: Int,
    val imageUrl: String,
    val description: String
)

@Entity(tableName = "cart")
data class CartEntity(
    @PrimaryKey(autoGenerate = true) val cartId: Int = 0,
    val userId: Int,
    val productId: Int,
    val productName: String,
    val price: Double,
    val qty: Int,
    val imageUrl: String
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val transactionId: Int = 0,
    val userId: Int,
    val total: Double,
    val status: String,
    val createdAt: Long = System.currentTimeMillis()
)
