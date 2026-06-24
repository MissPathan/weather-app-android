package com.misspathan.weatherapp.domain.usecase

import com.misspathan.weatherapp.domain.model.Result
import com.misspathan.weatherapp.domain.model.User
import com.misspathan.weatherapp.domain.model.WeatherInfo
import com.misspathan.weatherapp.domain.repository.AuthRepository
import com.misspathan.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentWeatherUseCase(private val repository: WeatherRepository) {
    suspend operator fun invoke(lat: Double, lon: Double): Result<WeatherInfo> {
        return repository.getWeatherByLocation(lat, lon)
    }
}

class GetWeatherByCityUseCase(private val repository: WeatherRepository) {
    suspend operator fun invoke(cityName: String): Result<WeatherInfo> {
        if (cityName.isBlank()) return Result.Error("City name cannot be empty")
        return repository.getWeatherByCity(cityName.trim())
    }
}

class SaveWeatherUseCase(private val repository: WeatherRepository) {
    suspend operator fun invoke(weather: WeatherInfo) {
        repository.saveWeatherToHistory(weather)
    }
}

class GetWeatherHistoryUseCase(private val repository: WeatherRepository) {
    operator fun invoke(): Flow<List<WeatherInfo>> {
        return repository.getWeatherHistory()
    }
}

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (email.isBlank() || password.isBlank()) {
            return Result.Error("Email and password are required")
        }
        return repository.login(email.trim(), password)
    }
}

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(name: String, email: String, password: String): Result<User> {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            return Result.Error("All fields are required")
        }
        if (password.length < 6) {
            return Result.Error("Password must be at least 6 characters")
        }
        // basic email check — not a full RFC 5322 regex, just enough
        if (!email.contains("@") || !email.contains(".")) {
            return Result.Error("Enter a valid email address")
        }
        return repository.register(name.trim(), email.trim(), password)
    }
}
