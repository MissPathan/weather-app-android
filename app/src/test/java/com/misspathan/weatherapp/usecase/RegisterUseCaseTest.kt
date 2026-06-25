package com.misspathan.weatherapp.usecase

import com.misspathan.weatherapp.domain.model.Result
import com.misspathan.weatherapp.domain.model.User
import com.misspathan.weatherapp.domain.repository.AuthRepository
import com.misspathan.weatherapp.domain.usecase.RegisterUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RegisterUseCaseTest {

    private lateinit var repository: AuthRepository
    private lateinit var useCase: RegisterUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = RegisterUseCase(repository)
    }

    @Test
    fun `returns error when name is blank`() = runTest {
        val result = useCase("", "test@email.com", "password123")
        assertTrue(result is Result.Error)
        assertEquals("All fields are required", (result as Result.Error).message)
        // repository should never be called for invalid input
        coVerify(exactly = 0) { repository.register(any(), any(), any()) }
    }

    @Test
    fun `returns error when password is too short`() = runTest {
        val result = useCase("Jane", "jane@email.com", "abc")
        assertTrue(result is Result.Error)
        assertEquals("Password must be at least 6 characters", (result as Result.Error).message)
    }

    @Test
    fun `returns error for invalid email format`() = runTest {
        val result = useCase("Jane", "notanemail", "password123")
        assertTrue(result is Result.Error)
        assertEquals("Enter a valid email address", (result as Result.Error).message)
    }

    @Test
    fun `calls repository when all input is valid`() = runTest {
        val fakeUser = User(1, "Jane", "jane@email.com")
        coEvery { repository.register("Jane", "jane@email.com", "password123") } returns
                Result.Success(fakeUser)

        val result = useCase("Jane", "jane@email.com", "password123")

        assertTrue(result is Result.Success)
        assertEquals("Jane", (result as Result.Success).data.name)
        coVerify(exactly = 1) { repository.register("Jane", "jane@email.com", "password123") }
    }
}
