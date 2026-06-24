package com.example.janagroandroid.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.janagroandroid.data.local.entity.CartEntity

@Dao
interface CartDao {
    @Query("""
        SELECT c.id, c.userId, c.productId, c.productName, c.price, c.imageUrl, c.qty, 
               COALESCE(p.merchant_id, c.merchantId, 0) as merchantId, 
               COALESCE(NULLIF(p.merchant_name, ''), NULLIF(c.merchantName, ''), 'Toko Tani Makmur') as merchantName
        FROM cart c 
        LEFT JOIN products p ON c.productId = p.id 
        WHERE c.userId = :userId ORDER BY c.id DESC
    """)
    fun getByUser(userId: Long): LiveData<List<CartEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CartEntity)

    @Delete
    suspend fun delete(item: CartEntity)

    @Query("DELETE FROM cart WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM cart WHERE userId = :userId")
    suspend fun clearByUser(userId: Long)
}