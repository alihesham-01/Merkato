package com.example.mekato_tessst

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class cart_Entity (
    @PrimaryKey(autoGenerate = true) var productId: Int = 1,
    var quantity: Int = 0,
    val image:String,
    val title_cart:String,
    val price_cart:Double,
    var total_cart:Double,
)
