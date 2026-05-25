package com.example.janagroandroid.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.janagroandroid.data.local.entity.TransactionEntity

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: TransactionEntity): Long

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY createdAt DESC")
    fun getHistory(userId: Int): LiveData<List<TransactionEntity>>
}