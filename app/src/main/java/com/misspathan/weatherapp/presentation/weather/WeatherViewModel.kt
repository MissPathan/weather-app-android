package com.misspathan.weatherapp.presentation.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.misspathan.weatherapp.domain.model.Result
import com.misspathan.weatherapp.domain.model.WeatherInfo
import com.misspathan.weatherapp.domain.usecase.GetCurrentWeatherUseCase
import com.misspathan.weatherapp.domain.usecase.GetWeatherByCityUseCase
import com.misspathan.weatherapp.domain.usecase.GetWeatherHistoryUseCase
import com.misspathan.weatherapp.domain.usecase.SaveWeatherUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WeatherUiState(
    val isLoading: Boolean = false,
    val weather: WeatherInfo? = null,
    val errorMessage: String? = null
)

data class HistoryUiState(
    val items: List<WeatherInfo> = emptyList(),
    val isLoading: Boolean = true
)

class WeatherViewModel(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getWeatherByCityUseCase: GetWeatherByCityUseCase,
    private val saveWeatherUseCase: SaveWeatherUseCase,
    private val getWeatherHistoryUseCase: GetWeatherHistoryUseCase
) : ViewModel() {

    private val _weatherState = MutableStateFlow(WeatherUiState())
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()

    private val _historyState = MutableStateFlow(HistoryUiState())
    val historyState: StateFlow<HistoryUiState> = _historyState.asStateFlow()

    // tracks whether we've already fetched + saved on this app open
    // resets only when ViewModel is cleared (i.e. process death = new app open)
    private var hasRecordedThisSession = false

    init {
        observeHistory()
    }

    fun fetchWeatherByLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            _weatherState.value = _weatherState.value.copy(isLoading = true, errorMessage = null)
            when (val result = getCurrentWeatherUseCase(lat, lon)) {
                is Result.Success -> {
                    _weatherState.value = _weatherState.value.copy(
                        isLoading = false,
                        weather = result.data
                    )
                    // only save once per session — tab switches don't add new records
                    if (!hasRecordedThisSession) {
                        saveWeatherUseCase(result.data)
                        hasRecordedThisSession = true
                    }
                }
                is Result.Error -> {
                    _weatherState.value = _weatherState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                Result.Loading -> Unit
            }
        }
    }

    fun fetchWeatherByCity(city: String) {
        viewModelScope.launch {
            _weatherState.value = _weatherState.value.copy(isLoading = true, errorMessage = null)
            when (val result = getWeatherByCityUseCase(city)) {
                is Result.Success -> {
                    _weatherState.value = _weatherState.value.copy(
                        isLoading = false,
                        weather = result.data
                    )
                    // manual city search also saves — but only if no session record yet
                    if (!hasRecordedThisSession) {
                        saveWeatherUseCase(result.data)
                        hasRecordedThisSession = true
                    }
                }
                is Result.Error -> {
                    _weatherState.value = _weatherState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                Result.Loading -> Unit
            }
        }
    }

    fun clearError() {
        _weatherState.value = _weatherState.value.copy(errorMessage = null)
    }

    private fun observeHistory() {
        viewModelScope.launch {
            getWeatherHistoryUseCase().collect { list ->
                _historyState.value = HistoryUiState(items = list, isLoading = false)
            }
        }
    }

    class Factory(
        private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
        private val getWeatherByCityUseCase: GetWeatherByCityUseCase,
        private val saveWeatherUseCase: SaveWeatherUseCase,
        private val getWeatherHistoryUseCase: GetWeatherHistoryUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WeatherViewModel(
                getCurrentWeatherUseCase,
                getWeatherByCityUseCase,
                saveWeatherUseCase,
                getWeatherHistoryUseCase
            ) as T
        }
    }
}
