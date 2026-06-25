package com.misspathan.weatherapp.data.remote.repository

import com.misspathan.weatherapp.BuildConfig
import com.misspathan.weatherapp.data.local.dao.WeatherHistoryDao
import com.misspathan.weatherapp.data.remote.api.WeatherApiService
import com.misspathan.weatherapp.domain.model.Result
import com.misspathan.weatherapp.domain.model.WeatherInfo
import com.misspathan.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WeatherRepositoryImpl(
    private val apiService: WeatherApiService,
    private val weatherHistoryDao: WeatherHistoryDao
) : WeatherRepository {

    override suspend fun getWeatherByLocation(lat: Double, lon: Double): Result<WeatherInfo> {
        return try {
            val response = apiService.getCurrentWeather(
                lat = lat,
                lon = lon,
                apiKey = BuildConfig.WEATHER_API_KEY
            )
            Result.Success(response.toDomain())
        } catch (e: retrofit2.HttpException) {
            val msg = when (e.code()) {
                401 -> "Invalid API key. Check your local.properties."
                404 -> "Location not found."
                429 -> "API limit reached. Try again later."
                else -> "Server error (${e.code()})"
            }
            Result.Error(msg, e)
        } catch (e: java.io.IOException) {
            Result.Error("No internet connection.", e)
        } catch (e: Exception) {
            Result.Error("Something went wrong: ${e.message}", e)
        }
    }

    override suspend fun getWeatherByCity(cityName: String): Result<WeatherInfo> {
        return try {
            val response = apiService.getWeatherByCity(
                cityName = cityName,
                apiKey = BuildConfig.WEATHER_API_KEY
            )
            Result.Success(response.toDomain())
        } catch (e: retrofit2.HttpException) {
            val msg = if (e.code() == 404) "City \"$cityName\" not found." else "Error: ${e.code()}"
            Result.Error(msg, e)
        } catch (e: java.io.IOException) {
            Result.Error("No internet connection.", e)
        } catch (e: Exception) {
            Result.Error("Something went wrong.", e)
        }
    }

    override suspend fun saveWeatherToHistory(weather: WeatherInfo) {
        weatherHistoryDao.insertWeather(weather.toEntity())
    }

    override fun getWeatherHistory(): Flow<List<WeatherInfo>> {
        return weatherHistoryDao.getAllWeatherHistory().map { list ->
            list.map { it.toDomain() }
        }
    }
}
