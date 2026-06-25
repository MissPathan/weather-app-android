package com.misspathan.weatherapp.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")   // home holds the bottom tab nav
}
