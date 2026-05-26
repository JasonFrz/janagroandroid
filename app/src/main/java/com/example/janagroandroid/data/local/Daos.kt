package com.example.janagroandroid.data.local

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {
    @Insert suspend fun insert(user: UserEntity): Long
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): UserEntity?
    @Query("SELECT * FROM users WHERE userId = :id LIMIT 1")
    suspend fun getById(id: Int): UserEntity?
}

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ProductEntity>)
    @Insert suspend fun insert(item: ProductEntity): Long
    @Query("SELECT * FROM products ORDER BY productId DESC")
    fun getAll(): LiveData<List<ProductEntity>>
    @Query("SELECT * FROM products WHERE productId = :id LIMIT 1")
    suspend fun getById(id: Int): ProductEntity?
    @Query("SELECT * FROM products WHERE sellerId = :sellerId ORDER BY productId DESC")
    fun getBySeller(sellerId: Int): LiveData<List<ProductEntity>>
}

@Dao
interface CartDao {
    @Insert suspend fun insert(item: CartEntity): Long
    @Query("SELECT * FROM cart WHERE userId = :userId ORDER BY cartId DESC")
    fun getCart(userId: Int): LiveData<List<CartEntity>>
    @Query("DELETE FROM cart WHERE cartId = :id") suspend fun delete(id: Int)
    @Query("DELETE FROM cart WHERE userId = :userId") suspend fun clear(userId: Int)
}

@Dao
interface TransactionDao {
    @Insert suspend fun insert(item: TransactionEntity): Long
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY createdAt DESC")
    fun getHistory(userId: Int): LiveData<List<TransactionEntity>>
}
