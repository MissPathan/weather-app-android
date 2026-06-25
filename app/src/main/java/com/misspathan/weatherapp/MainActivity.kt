package com.misspathan.weatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.misspathan.weatherapp.presentation.HomeScreen
import com.misspathan.weatherapp.presentation.auth.AuthViewModel
import com.misspathan.weatherapp.presentation.auth.LoginScreen
import com.misspathan.weatherapp.presentation.auth.RegisterScreen
import com.misspathan.weatherapp.presentation.navigation.Screen
import com.misspathan.weatherapp.presentation.weather.WeatherViewModel
import com.misspathan.weatherapp.theme.WeatherAppTheme
import com.misspathan.weatherapp.util.SessionManager

class MainActivity : ComponentActivity() {

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private var onLocationResult: ((Double, Double) -> Unit)? = null

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            fetchLocation()
        } else {
            Toast.makeText(this, "Location permission denied. Search by city name.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                AppNavGraph()
            }
        }
    }

    @Composable
    private fun AppNavGraph() {
        val navController = rememberNavController()
        val app = application as WeatherApp

        val startDestination = if (SessionManager.isLoggedIn()) Screen.Home.route else Screen.Login.route

        val authViewModel: AuthViewModel = viewModel(
            factory = AuthViewModel.Factory(app.container.loginUseCase, app.container.registerUseCase)
        )

        val weatherViewModel: WeatherViewModel = viewModel(
            factory = WeatherViewModel.Factory(
                app.container.getCurrentWeatherUseCase,
                app.container.getWeatherByCityUseCase,
                app.container.saveWeatherUseCase,
                app.container.getWeatherHistoryUseCase
            )
        )

        NavHost(navController = navController, startDestination = startDestination) {

            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    viewModel = authViewModel,
                    onNavigateToLogin = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    weatherViewModel = weatherViewModel,
                    onRequestLocation = { requestLocationPermission(weatherViewModel) },
                    onLogout = {
                        // clear entire back stack so back button doesn't return to Home after logout
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }

    private fun requestLocationPermission(viewModel: WeatherViewModel) {
        val fineGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            onLocationResult = { lat, lon -> viewModel.fetchWeatherByLocation(lat, lon) }
            fetchLocation()
        } else {
            onLocationResult = { lat, lon -> viewModel.fetchWeatherByLocation(lat, lon) }
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun fetchLocation() {
        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        onLocationResult?.invoke(location.latitude, location.longitude)
                    } else {
                        Toast.makeText(this, "Couldn't get location. Try searching by city.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Location error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Location permission required.", Toast.LENGTH_SHORT).show()
        }
    }
}
