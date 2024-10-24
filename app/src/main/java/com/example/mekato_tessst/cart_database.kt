package com.example.mekato_tessst

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database( entities = [cart_Entity::class], version = 1, exportSchema = false)

abstract class cart_database: RoomDatabase() {

    abstract fun cart_dao(): cart_dao

    companion object{
        @Volatile
        private var INSTANCE: cart_database? = null

        fun getinstance(context: Context): cart_database {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    cart_database::class.java,
                    "cart_database"

                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}
