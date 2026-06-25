package com.misspathan.weatherapp.usecase

import com.misspathan.weatherapp.domain.model.Result
import com.misspathan.weatherapp.domain.model.WeatherInfo
import com.misspathan.weatherapp.domain.repository.WeatherRepository
import com.misspathan.weatherapp.domain.usecase.GetCurrentWeatherUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetCurrentWeatherUseCaseTest {

    private lateinit var repository: WeatherRepository
    private lateinit var useCase: GetCurrentWeatherUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetCurrentWeatherUseCase(repository)
    }

    @Test
    fun `invoke returns success when repository succeeds`() = runTest {
        val fakeWeather = makeWeatherInfo()
        coEvery { repository.getWeatherByLocation(any(), any()) } returns Result.Success(fakeWeather)

        val result = useCase(14.5995, 120.9842)

        assertTrue(result is Result.Success)
        assertEquals(fakeWeather.cityName, (result as Result.Success).data.cityName)
        coVerify(exactly = 1) { repository.getWeatherByLocation(14.5995, 120.9842) }
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        coEvery { repository.getWeatherByLocation(any(), any()) } returns
                Result.Error("No internet connection.")

        val result = useCase(14.5995, 120.9842)

        assertTrue(result is Result.Error)
        assertEquals("No internet connection.", (result as Result.Error).message)
    }

    private fun makeWeatherInfo() = WeatherInfo(
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
