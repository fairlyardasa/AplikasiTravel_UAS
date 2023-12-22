package com.example.aplikasitravel_uas.favorite

import android.content.Context
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Favorite::class], version = 1, exportSchema = false)

abstract class FavoriteRoomDatabase : RoomDatabase() {

    abstract fun favoriteDao() : FavoriteDao?


    companion object {
        @Volatile
        private var INSTANCE: FavoriteRoomDatabase? = null

        fun getDatabase(context: Context): FavoriteRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavoriteRoomDatabase::class.java, "favorite_database"
                ).build()
                INSTANCE = instance
                instance
            } ?: error("Database initialization failed")
        }

    }


}