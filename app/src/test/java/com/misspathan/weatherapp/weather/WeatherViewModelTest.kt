package com.misspathan.weatherapp.weather

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.misspathan.weatherapp.domain.model.Result
import com.misspathan.weatherapp.domain.model.WeatherInfo
import com.misspathan.weatherapp.domain.usecase.GetCurrentWeatherUseCase
import com.misspathan.weatherapp.domain.usecase.GetWeatherByCityUseCase
import com.misspathan.weatherapp.domain.usecase.GetWeatherHistoryUseCase
import com.misspathan.weatherapp.domain.usecase.SaveWeatherUseCase
import com.misspathan.weatherapp.presentation.weather.WeatherViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getCurrentWeather: GetCurrentWeatherUseCase
    private lateinit var getWeatherByCity: GetWeatherByCityUseCase
    private lateinit var saveWeather: SaveWeatherUseCase
    private lateinit var getHistory: GetWeatherHistoryUseCase
    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getCurrentWeather = mockk()
        getWeatherByCity = mockk()
        saveWeather = mockk(relaxed = true)
        getHistory = mockk()
        every { getHistory() } returns flowOf(emptyList())
        viewModel = WeatherViewModel(getCurrentWeather, getWeatherByCity, saveWeather, getHistory)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchWeatherByLocation updates state with weather on success`() = runTest {
        val fakeWeather = makeWeather()
        coEvery { getCurrentWeather(any(), any()) } returns Result.Success(fakeWeather)

        viewModel.fetchWeatherByLocation(14.5995, 120.9842)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.weatherState.value
        assertNotNull(state.weather)
        assertEquals("Manila", state.weather?.cityName)
        assertNull(state.errorMessage)
    }

    @Test
    fun `fetchWeatherByLocation sets error message on failure`() = runTest {
        coEvery { getCurrentWeather(any(), any()) } returns Result.Error("No internet connection.")

        viewModel.fetchWeatherByLocation(14.5995, 120.9842)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.weatherState.value
        assertNull(state.weather)
        assertEquals("No internet connection.", state.errorMessage)
    }

    @Test
    fun `fetchWeatherByLocation saves weather to history on success`() = runTest {
        val fakeWeather = makeWeather()
        coEvery { getCurrentWeather(any(), any()) } returns Result.Success(fakeWeather)

        viewModel.fetchWeatherByLocation(14.5995, 120.9842)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { saveWeather(fakeWeather) }
    }

    @Test
    fun `clearError resets error message`() = runTest {
        coEvery { getCurrentWeather(any(), any()) } returns Result.Error("Some error")
        viewModel.fetchWeatherByLocation(0.0, 0.0)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearError()

        assertNull(viewModel.weatherState.value.errorMessage)
    }

    private fun makeWeather() = WeatherInfo(
        cityName = "Manila",
        country = "PH",
        temperatureCelsius = 32.0,
        feelsLike = 36.0,
        humidity = 80,
        condition = "Clear",
        description = "clear sky",
        sunrise = 1718240000L,
        sunset = 1718280000L,
        fetchedAt = System.currentTimeMillis()
    )
}
