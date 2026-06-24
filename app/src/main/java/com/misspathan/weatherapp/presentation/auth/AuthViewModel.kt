package com.misspathan.weatherapp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.misspathan.weatherapp.domain.model.Result
import com.misspathan.weatherapp.domain.usecase.LoginUseCase
import com.misspathan.weatherapp.domain.usecase.RegisterUseCase
import com.misspathan.weatherapp.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val userName: String = ""
)

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = loginUseCase(email, password)) {
                is Result.Success -> {
                    SessionManager.login(result.data.id, result.data.name)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        userName = result.data.name
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                Result.Loading -> Unit
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = registerUseCase(name, email, password)) {
                is Result.Success -> {
                    SessionManager.login(result.data.id, result.data.name)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        userName = result.data.name
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                Result.Loading -> Unit
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    class Factory(
        private val loginUseCase: LoginUseCase,
        private val registerUseCase: RegisterUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthViewModel(loginUseCase, registerUseCase) as T
        }
    }
}
