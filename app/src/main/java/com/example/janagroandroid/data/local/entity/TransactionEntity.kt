package com.example.janagroandroid.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val transactionId: Int = 0,
    val userId: Int,
    val total: Double,
    val status: String,
    val createdAt: Long = System.currentTimeMillis()
)