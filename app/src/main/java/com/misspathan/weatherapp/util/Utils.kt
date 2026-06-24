package com.misspathan.weatherapp.util

import android.content.Context
import com.misspathan.weatherapp.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object WeatherIconMapper {

    // current weather — uses device clock (live)
    fun getWeatherIcon(condition: String): Int {
        return resolveIcon(condition, isPastSixPM(System.currentTimeMillis()))
    }

    // history — uses the timestamp of when the record was fetched
    fun getWeatherIconForTime(condition: String, fetchedAtMillis: Long): Int {
        return resolveIcon(condition, isPastSixPM(fetchedAtMillis))
    }

    private fun resolveIcon(condition: String, isPastSix: Boolean): Int {
        return when (condition.lowercase()) {
            "clear" -> if (isPastSix) R.drawable.ic_moon else R.drawable.ic_sun
            "clouds" -> R.drawable.ic_cloudy
            "rain", "drizzle" -> R.drawable.ic_rain
            "thunderstorm" -> R.drawable.ic_thunderstorm
            "snow" -> R.drawable.ic_snow
            "mist", "fog", "haze", "smoke", "dust", "sand", "ash", "squall", "tornado" -> R.drawable.ic_foggy
            else -> if (isPastSix) R.drawable.ic_moon else R.drawable.ic_sun
        }
    }

    private fun isPastSixPM(timestampMillis: Long): Boolean {
        val cal = Calendar.getInstance().apply { timeInMillis = timestampMillis }
        return cal.get(Calendar.HOUR_OF_DAY) >= 18
    }
}

object TimeFormatter {
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    fun formatTime(timestampMillis: Long): String = timeFormat.format(Date(timestampMillis))
    fun formatDateTime(timestampMillis: Long): String = dateTimeFormat.format(Date(timestampMillis))
    fun formatTemperature(temp: Double): String = "%.1f°C".format(temp)
}

object SessionManager {

    private const val PREFS_NAME = "weather_app_session"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val NO_USER = -1

    private lateinit var prefs: android.content.SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun login(userId: Int, userName: String) {
        prefs.edit()
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_USER_NAME, userName)
            .apply()
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = prefs.getInt(KEY_USER_ID, NO_USER) != NO_USER

    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "") ?: ""
}
