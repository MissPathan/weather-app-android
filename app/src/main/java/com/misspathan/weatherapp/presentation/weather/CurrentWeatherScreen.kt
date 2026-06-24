package com.misspathan.weatherapp.presentation.weather

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.misspathan.weatherapp.domain.model.WeatherInfo
import com.misspathan.weatherapp.util.TimeFormatter
import com.misspathan.weatherapp.util.WeatherIconMapper

@Composable
fun CurrentWeatherScreen(
    viewModel: WeatherViewModel,
    onRequestLocation: () -> Unit
) {
    val weatherState by viewModel.weatherState.collectAsState()
    var cityInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        onRequestLocation()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0D47A1), Color(0xFF1565C0), Color(0xFF1976D2))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = cityInput,
                onValueChange = { cityInput = it },
                placeholder = { Text("Search city...", color = Color.White.copy(alpha = 0.6f)) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (cityInput.isNotBlank()) {
                            viewModel.fetchWeatherByCity(cityInput)
                            focusManager.clearFocus()
                        }
                    }
                ),
                trailingIcon = {
                    IconButton(onClick = { onRequestLocation() }) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Use my location",
                            tint = Color.White
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            when {
                weatherState.isLoading -> {
                    Spacer(modifier = Modifier.height(80.dp))
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Fetching weather...", color = Color.White.copy(alpha = 0.7f))
                }

                weatherState.errorMessage != null -> {
                    ErrorCard(
                        message = weatherState.errorMessage!!,
                        onRetry = { onRequestLocation() }
                    )
                }

                weatherState.weather != null -> {
                    WeatherContent(weather = weatherState.weather!!)
                }

                else -> {
                    Spacer(modifier = Modifier.height(80.dp))
                    Text(
                        text = "Tap the location icon\nor search for a city",
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun WeatherContent(weather: WeatherInfo) {
    val iconRes = WeatherIconMapper.getWeatherIcon(weather.condition)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.LocationOn,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "${weather.cityName}, ${weather.country}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Image(
        painter = painterResource(id = iconRes),
        contentDescription = weather.condition,
        modifier = Modifier.size(120.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = TimeFormatter.formatTemperature(weather.temperatureCelsius),
        fontSize = 72.sp,
        fontWeight = FontWeight.Thin,
        color = Color.White
    )

    Text(
        text = weather.description.replaceFirstChar { it.uppercase() },
        fontSize = 18.sp,
        color = Color.White.copy(alpha = 0.85f),
        modifier = Modifier.padding(top = 4.dp)
    )

    Text(
        text = "Feels like ${TimeFormatter.formatTemperature(weather.feelsLike)}",
        fontSize = 14.sp,
        color = Color.White.copy(alpha = 0.65f),
        modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DetailCard(modifier = Modifier.weight(1f), label = "Humidity", value = "${weather.humidity}%")
        DetailCard(modifier = Modifier.weight(1f), label = "Sunrise", value = TimeFormatter.formatTime(weather.sunrise))
        DetailCard(modifier = Modifier.weight(1f), label = "Sunset", value = TimeFormatter.formatTime(weather.sunset))
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Updated: ${TimeFormatter.formatDateTime(weather.fetchedAt)}",
        fontSize = 12.sp,
        color = Color.White.copy(alpha = 0.5f)
    )
}

@Composable
private fun DetailCard(modifier: Modifier = Modifier, label: String, value: String) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
    }
}

@Composable
private fun ErrorCard(message: String, onRetry: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.2f),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Retry")
            }
        }
    }
}
