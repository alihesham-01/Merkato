package com.example.mekato_tessst

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface cart_dao {
    @Insert
    fun insert(item: cart_Entity)

    @Delete
    fun delete(item: cart_Entity)

    @Query("DELETE FROM cart WHERE productId = :productId")
    fun deleteProductById(productId: Int)

    @Query("SELECT * FROM cart")
    suspend fun getAll(): List<cart_Entity>

    @Query("DELETE FROM cart")
    fun deleteAll()


    @Query("SELECT SUM(total_cart) FROM cart")
    suspend fun getSum():Double

    @Update
    fun update(item: cart_Entity)

    @Query("SELECT * FROM cart WHERE productId = :productId")
    suspend fun getProductById(productId: Int): cart_Entity


}