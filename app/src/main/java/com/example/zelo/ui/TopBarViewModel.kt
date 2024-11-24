package com.example.zelo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.zelo.MyApplication
import com.example.zelo.network.SessionManager
import com.example.zelo.network.dataSources.DataSourceException
import com.example.zelo.network.model.Error
import com.example.zelo.network.model.User
import com.example.zelo.network.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TopBarUiState(
    val isAuthenticated: Boolean = false,
    val user: User? = null,
    val isFetching: Boolean = false,
    val error: Error? = null,
    val currentSection: String = ""
)

class TopBarViewModel(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(TopBarUiState())
    val uiState: StateFlow<TopBarUiState> = _uiState.asStateFlow()

    init {
        observeLogoutSignal()
        checkAuthenticationStatus()
    }

    private fun observeLogoutSignal() {
        viewModelScope.launch {
            sessionManager.logoutSignal.collect {
                logout()
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            _uiState.value = TopBarUiState()
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isFetching = true, error = null)
            try {
                val user = userRepository.getCurrentUser(true)
                _uiState.value = _uiState.value.copy(user = user, isAuthenticated = true, isFetching = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = handleError(e), isFetching = false)
            }
        }
    }

    fun checkAuthenticationStatus() {
        val isAuthenticated = sessionManager.loadAuthToken() != null
        if (isAuthenticated) {
            getCurrentUser()
        } else {
            _uiState.value = _uiState.value.copy(isAuthenticated = false, user = null)
        }
    }

    fun updateCurrentSection(section: String) {
        _uiState.value = _uiState.value.copy(currentSection = section)
    }

    private fun handleError(e: Throwable): Error {
        return if (e is DataSourceException) {
            Error(e.code, e.message ?: "")
        } else {
            Error(null, e.message ?: "")
        }
    }

    companion object {
        fun provideFactory(
            application: MyApplication
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TopBarViewModel(
                    application.userRepository,
                    application.sessionManager
                ) as T
            }
        }
    }
}

