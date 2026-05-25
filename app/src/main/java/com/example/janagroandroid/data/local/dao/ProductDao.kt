package com.example.janagroandroid.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.janagroandroid.data.local.entity.ProductEntity

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ProductEntity>)

    @Query("SELECT * FROM products ORDER BY productId DESC")
    fun getAll(): LiveData<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE productId = :id LIMIT 1")
    suspend fun getById(id: Int): ProductEntity?

    @Query("SELECT * FROM products WHERE sellerId = :sellerId ORDER BY productId DESC")
    fun getBySeller(sellerId: Int): LiveData<List<ProductEntity>>
}