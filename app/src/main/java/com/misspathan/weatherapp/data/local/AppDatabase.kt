package com.misspathan.weatherapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.misspathan.weatherapp.data.local.dao.UserDao
import com.misspathan.weatherapp.data.local.dao.WeatherHistoryDao
import com.misspathan.weatherapp.data.local.entity.UserEntity
import com.misspathan.weatherapp.data.local.entity.WeatherHistoryEntity

@Database(
    entities = [UserEntity::class, WeatherHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun weatherHistoryDao(): WeatherHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            // double-checked locking — standard singleton pattern for Room
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "weather_app.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
