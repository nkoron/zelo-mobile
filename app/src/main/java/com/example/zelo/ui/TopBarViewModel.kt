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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


data class TopBarUiState (
    val isAuthenticated: Boolean = false,
    val user: User? = null,
    val isFetching: Boolean = false,
    val error: Error? = null
)
class TopBarViewModel (
        private val userRepository: UserRepository,
    sessionManager: SessionManager,
    ) : ViewModel() {

        var uiState by mutableStateOf(TopBarUiState(isAuthenticated = sessionManager.loadAuthToken() != null))
            private set
        init {
            if (uiState.isAuthenticated) {
                getCurrentUser()
            }
        }

        fun getCurrentUser() = runOnViewModelScope(
            {
                userRepository.getCurrentUser()
            },
            { state, response -> state.copy(user = response) }
        )


    private fun <R> runOnViewModelScope(
        block: suspend () -> R,
        updateState: (TopBarUiState, R) -> TopBarUiState
    ): Job = viewModelScope.launch {
        uiState = uiState.copy(isFetching = true, error = null)
        runCatching {
            block()
        }.onSuccess { response ->
            uiState = updateState(uiState, response).copy(isFetching = false)
        }.onFailure { e ->
            uiState = uiState.copy(isFetching = false, error = handleError(e))
        }
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
                override fun<T : ViewModel> create(modelClass: Class<T>): T {
                    return TopBarViewModel(
                        application.userRepository,
                        application.sessionManager
                        ) as T
                }
            }
        }

    }