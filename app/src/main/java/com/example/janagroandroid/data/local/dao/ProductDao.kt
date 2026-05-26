package com.example.janagroandroid.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.janagroandroid.data.local.entity.ProductEntity

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAll(): LiveData<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE merchant_id = :merchantId ORDER BY id DESC")
    fun getSellerProducts(merchantId: Long): LiveData<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Query("DELETE FROM products")
    suspend fun clearAll()
}