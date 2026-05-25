package com.example.janagroandroid.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.janagroandroid.data.local.entity.CartEntity

@Dao
interface CartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CartEntity): Long

    @Query("SELECT * FROM cart WHERE userId = :userId ORDER BY cartId DESC")
    fun getCart(userId: Int): LiveData<List<CartEntity>>

    @Query("DELETE FROM cart WHERE cartId = :cartId")
    suspend fun delete(cartId: Int)

    @Query("DELETE FROM cart WHERE userId = :userId")
    suspend fun clear(userId: Int)
}