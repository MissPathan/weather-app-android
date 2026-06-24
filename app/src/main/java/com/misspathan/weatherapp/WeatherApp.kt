package com.misspathan.weatherapp

import android.app.Application
import com.misspathan.weatherapp.di.AppContainer
import com.misspathan.weatherapp.util.SessionManager

class WeatherApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()

        container = AppContainer(this)
    }
}
