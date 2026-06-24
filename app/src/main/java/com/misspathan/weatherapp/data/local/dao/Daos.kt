package com.misspathan.weatherapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.misspathan.weatherapp.data.local.entity.UserEntity
import com.misspathan.weatherapp.data.local.entity.WeatherHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    suspend fun emailExists(email: String): Int
}

@Dao
interface WeatherHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherHistoryEntity)

    // newest first, cap at 50 so the list doesn't grow forever
    @Query("SELECT * FROM weather_history ORDER BY fetchedAt DESC LIMIT 50")

    fun getAllWeatherHistory(): Flow<List<WeatherHistoryEntity>>

    @Query("SELECT COUNT(*) FROM weather_history")
    suspend fun getCount(): Int
}
