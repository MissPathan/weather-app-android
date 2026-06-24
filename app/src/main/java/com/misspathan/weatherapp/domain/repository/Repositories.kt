package com.misspathan.weatherapp.domain.repository

import com.misspathan.weatherapp.domain.model.Result
import com.misspathan.weatherapp.domain.model.User
import com.misspathan.weatherapp.domain.model.WeatherInfo
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getWeatherByLocation(lat: Double, lon: Double): Result<WeatherInfo>
    suspend fun getWeatherByCity(cityName: String): Result<WeatherInfo>
    suspend fun saveWeatherToHistory(weather: WeatherInfo)
    fun getWeatherHistory(): Flow<List<WeatherInfo>>
}

interface AuthRepository {
    suspend fun register(name: String, email: String, password: String): Result<User>
    suspend fun login(email: String, password: String): Result<User>
}
