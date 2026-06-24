package com.misspathan.weatherapp.data.repository

import com.misspathan.weatherapp.data.local.entity.UserEntity
import com.misspathan.weatherapp.data.local.entity.WeatherHistoryEntity
import com.misspathan.weatherapp.data.remote.dto.WeatherResponseDto
import com.misspathan.weatherapp.domain.model.User
import com.misspathan.weatherapp.domain.model.WeatherInfo

fun WeatherResponseDto.toDomain(): WeatherInfo {
    return WeatherInfo(
        cityName = this.cityName,
        country = this.sys.country,
        temperatureCelsius = this.main.temp,
        feelsLike = this.main.feelsLike,
        humidity = this.main.humidity,
        condition = this.weather.firstOrNull()?.main ?: "Unknown",
        description = this.weather.firstOrNull()?.description ?: "",
        sunrise = this.sys.sunrise * 1000L,   // convert to millis
        sunset = this.sys.sunset * 1000L,
        fetchedAt = System.currentTimeMillis()
    )
}

fun WeatherInfo.toEntity(): WeatherHistoryEntity {
    return WeatherHistoryEntity(
        cityName = this.cityName,
        country = this.country,
        temperature = this.temperatureCelsius,
        weatherCondition = this.condition,
        description = this.description,
        humidity = this.humidity,
        sunrise = this.sunrise,
        sunset = this.sunset,
        fetchedAt = this.fetchedAt
    )
}

fun WeatherHistoryEntity.toDomain(): WeatherInfo {
    return WeatherInfo(
        cityName = this.cityName,
        country = this.country,
        temperatureCelsius = this.temperature,
        feelsLike = 0.0,
        humidity = this.humidity,
        condition = this.weatherCondition,
        description = this.description,
        sunrise = this.sunrise,
        sunset = this.sunset,
        fetchedAt = this.fetchedAt
    )
}

fun UserEntity.toDomain(): User {
    return User(id = this.id, name = this.name, email = this.email)
}
