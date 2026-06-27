package com.example.janagroandroid.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.janagroandroid.data.local.entity.CartEntity

@Dao
interface CartDao {
    @Query("""
        SELECT c.id, c.userId, c.productId, c.productName, c.price, c.imageUrl, c.qty, 
               c.merchantId as merchantId, 
               COALESCE(NULLIF(c.merchantName, ''), 'Toko Tani Makmur') as merchantName
        FROM cart c 
        WHERE c.userId = :userId ORDER BY c.id DESC
    """)
    fun getByUser(userId: Long): LiveData<List<CartEntity>>

    @Query("""
        SELECT c.id, c.userId, c.productId, c.productName, c.price, c.imageUrl, c.qty, 
               c.merchantId as merchantId, 
               COALESCE(NULLIF(c.merchantName, ''), 'Toko Tani Makmur') as merchantName
        FROM cart c 
        WHERE c.id IN (:ids)
    """)
    suspend fun getItemsByIds(ids: LongArray): List<CartEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CartEntity)

    @Delete
    suspend fun delete(item: CartEntity)

    @Query("DELETE FROM cart WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM cart WHERE userId = :userId")
    suspend fun clearByUser(userId: Long)
}