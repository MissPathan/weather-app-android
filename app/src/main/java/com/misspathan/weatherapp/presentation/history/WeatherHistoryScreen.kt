package com.misspathan.weatherapp.presentation.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.misspathan.weatherapp.domain.model.WeatherInfo
import com.misspathan.weatherapp.presentation.weather.WeatherViewModel
import com.misspathan.weatherapp.util.TimeFormatter
import com.misspathan.weatherapp.util.WeatherIconMapper

@Composable
fun WeatherHistoryScreen(viewModel: WeatherViewModel) {
    val historyState by viewModel.historyState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0D47A1), Color(0xFF1565C0))
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Text(
                text = "Weather History",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 4.dp)
            )
            Text(
                text = "Fetched each time you opened the app",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
            )

            when {
                historyState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }

                historyState.items.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No history yet.\nOpen the app a few times to see records here.",
                            color = Color.White.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(32.dp)
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(historyState.items, key = { it.fetchedAt }) { item ->
                            HistoryItemCard(item)
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryItemCard(weather: WeatherInfo) {
    val iconRes = WeatherIconMapper.getWeatherIconForTime(weather.condition, weather.fetchedAt)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = weather.condition,
                modifier = Modifier.size(44.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${weather.cityName}, ${weather.country}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = weather.description.replaceFirstChar { it.uppercase() },
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.65f)
                )
                Text(
                    text = TimeFormatter.formatDateTime(weather.fetchedAt),
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.45f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Text(
                text = TimeFormatter.formatTemperature(weather.temperatureCelsius),
                fontSize = 22.sp,
                fontWeight = FontWeight.Light,
                color = Color.White
            )
        }
    }
}
