package com.misspathan.weatherapp

import android.app.Application
import com.misspathan.weatherapp.di.AppContainer
import com.misspathan.weatherapp.util.SessionManager

class WeatherApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        // SessionManager must be initialised before anything reads isLoggedIn()
        SessionManager.init(this)
        container = AppContainer(this)
    }
}
