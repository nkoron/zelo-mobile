package com.example.zelo.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.zelo.MyApplication
import com.example.zelo.network.SessionManager
import com.example.zelo.network.dataSources.DataSourceException
import com.example.zelo.network.model.Error
import com.example.zelo.network.model.User
import com.example.zelo.network.model.WalletDetails
import com.example.zelo.network.repository.PaymentRepository
import com.example.zelo.network.repository.UserRepository
import com.example.zelo.network.repository.WalletRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class DashboardUiState (
    val isAuthenticated: Boolean = false,
    val user: User? = null,
    val isFetching: Boolean = false,
    val walletDetail: WalletDetails? = null,
    val error: Error? = null
)

class DashboardViewModel(
    private val walletRepository: WalletRepository,
    sessionManager: SessionManager,
    private val userRepository: UserRepository,
    private val paymentRepository: PaymentRepository
) : ViewModel() {


    private var walletDetailStreamJob: Job? = null
    private val _uiState = MutableStateFlow(DashboardUiState(isAuthenticated = sessionManager.loadAuthToken() != null))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        if (uiState.value.isAuthenticated) {
            observeWalletDetailStream()
        }
    }

    fun login(username: String, password: String) = runOnViewModelScope(
        {
            userRepository.login(username, password)
            observeWalletDetailStream()
        },
        { state, _ -> state.copy(isAuthenticated = true) }
    )

    fun logout() = runOnViewModelScope(
        {
            walletDetailStreamJob?.cancel()
            userRepository.logout()
        },
        { state, _ ->
            state.copy(
                isAuthenticated = false,
                walletDetail = null
            )
        }
    )

    private fun observeWalletDetailStream() {
        walletDetailStreamJob = collectOnViewModelScope(
            walletRepository.walletDetailStream
        ) { state, response -> state.copy(walletDetail = response) }
    }

    private fun <T> collectOnViewModelScope(
        flow: Flow<T>,
        updateState: (DashboardUiState, T) -> DashboardUiState
    ) = viewModelScope.launch {
        flow
            .distinctUntilChanged()
            .catch { e -> _uiState.update { currentState -> currentState.copy(error = handleError(e)) } }
            .collect { response -> _uiState.update { currentState -> updateState(currentState, response) } }
    }
    private fun <R> runOnViewModelScope(
        block: suspend () -> R,
        updateState: (DashboardUiState, R) -> DashboardUiState
    ): Job = viewModelScope.launch {
        _uiState.update { currentState -> currentState.copy(isFetching = true, error = null) }
        runCatching {
            block()
        }.onSuccess { response ->
            _uiState.update { currentState -> updateState(currentState, response).copy(isFetching = false) }
        }.onFailure { e ->
            _uiState.update { currentState -> currentState.copy(isFetching = false, error = handleError(e)) }
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
                return DashboardViewModel(
                    application.walletRepository,
                    application.sessionManager,
                    application.userRepository
                ) as T
            }
        }
    }

}
