package com.example.janagroandroid.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.janagroandroid.data.local.entity.HistoryEntity

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY id DESC")
    fun getAll(): LiveData<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: HistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<HistoryEntity>)

    @Query("DELETE FROM history")
    suspend fun clearAll()
}