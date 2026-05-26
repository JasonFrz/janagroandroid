package com.example.janagroandroid.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.janagroandroid.data.local.entity.CartEntity

@Dao
interface CartDao {
    @Query("SELECT * FROM cart WHERE userId = :userId ORDER BY id DESC")
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