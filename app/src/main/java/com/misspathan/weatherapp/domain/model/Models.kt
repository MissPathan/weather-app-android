package com.misspathan.weatherapp.domain.model

data class WeatherInfo(
    val cityName: String,
    val country: String,
    val temperatureCelsius: Double,
    val feelsLike: Double,
    val humidity: Int,
    val condition: String,
    val description: String,
    val sunrise: Long,
    val sunset: Long,
    val fetchedAt: Long
)

data class User(
    val id: Int,
    val name: String,
    val email: String
)


sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val cause: Throwable? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
