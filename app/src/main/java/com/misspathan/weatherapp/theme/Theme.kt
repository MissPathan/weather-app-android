package com.misspathan.weatherapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// using light scheme as base — our screens use custom backgrounds anyway
// this ensures dialogs, alerts, and system components use readable dark-on-light colors
private val AppColors = lightColorScheme(
    primary = Color(0xFF1A237E),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3949AB),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF1565C0),
    onSecondary = Color.White,
    background = Color(0xFF0D1B2A),
    onBackground = Color.White,
    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF444444),
    error = Color(0xFFB00020),
    onError = Color.White,
    outline = Color(0xFFBDBDBD)
)

@Composable
fun WeatherAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColors,
        content = content
    )
}
