package com.misspathan.weatherapp.di

import android.content.Context
import com.misspathan.weatherapp.BuildConfig
import com.misspathan.weatherapp.data.local.AppDatabase
import com.misspathan.weatherapp.data.remote.api.WeatherApiService
import com.misspathan.weatherapp.data.repository.AuthRepositoryImpl
import com.misspathan.weatherapp.data.repository.WeatherRepositoryImpl
import com.misspathan.weatherapp.domain.repository.AuthRepository
import com.misspathan.weatherapp.domain.repository.WeatherRepository
import com.misspathan.weatherapp.domain.usecase.GetCurrentWeatherUseCase
import com.misspathan.weatherapp.domain.usecase.GetWeatherByCityUseCase
import com.misspathan.weatherapp.domain.usecase.GetWeatherHistoryUseCase
import com.misspathan.weatherapp.domain.usecase.LoginUseCase
import com.misspathan.weatherapp.domain.usecase.RegisterUseCase
import com.misspathan.weatherapp.domain.usecase.SaveWeatherUseCase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AppContainer(context: Context) {

    private val db = AppDatabase.getInstance(context)

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .apply {
            // only log in debug — don't leak request details in release
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                )
            }
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.WEATHER_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val weatherApiService: WeatherApiService = retrofit.create(WeatherApiService::class.java)

    // repositories
    val weatherRepository: WeatherRepository = WeatherRepositoryImpl(
        apiService = weatherApiService,
        weatherHistoryDao = db.weatherHistoryDao()
    )

    val authRepository: AuthRepository = AuthRepositoryImpl(
        userDao = db.userDao()
    )

    // use cases
    val getCurrentWeatherUseCase = GetCurrentWeatherUseCase(weatherRepository)
    val getWeatherByCityUseCase = GetWeatherByCityUseCase(weatherRepository)
    val saveWeatherUseCase = SaveWeatherUseCase(weatherRepository)
    val getWeatherHistoryUseCase = GetWeatherHistoryUseCase(weatherRepository)
    val loginUseCase = LoginUseCase(authRepository)
    val registerUseCase = RegisterUseCase(authRepository)
}
