package com.example.zelo.enter_money

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.zelo.MyApplication
import com.example.zelo.network.SessionManager

import com.example.zelo.network.dataSources.DataSourceException
import com.example.zelo.network.model.Error
import com.example.zelo.network.model.User
import com.example.zelo.network.model.WalletDetails
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

data class DepositUiState(
    val walletDetail: WalletDetails? = null,
    val balance: Double,
    val error: Error? = null,
    val isFetching: Boolean = false
)

class DepositViewModel(
    private val walletRepository: WalletRepository,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private var walletDetailStreamJob: Job? = null
    private val _uiState = MutableStateFlow(DepositUiState(walletDetail = null, balance= 0.0))
    val uiState: StateFlow<DepositUiState> = _uiState.asStateFlow()

    init {
            observeWalletDetailStream()
        observeLogoutSignal()
    }
    private fun observeLogoutSignal() {
        viewModelScope.launch {
            sessionManager.logoutSignal.collect {
                walletDetailStreamJob?.cancel()
            }
        }
    }

    private fun observeWalletDetailStream() {
        walletDetailStreamJob = collectOnViewModelScope(
            walletRepository.walletDetailStream
        ) { state, response -> state.copy(walletDetail = response) }
    }


    fun rechargeWallet(amount: Double) = runOnViewModelScope(
        {
            walletRepository.rechargeWallet(amount)
        },
        {state, response -> state.copy(balance = response)}
    )


    private fun <T> collectOnViewModelScope(
        flow: Flow<T>,
        updateState: (DepositUiState, T) -> DepositUiState
    ) = viewModelScope.launch {
        flow
            .distinctUntilChanged()
            .catch { e -> _uiState.update { currentState -> currentState.copy(error = handleError(e)) } }
            .collect { response -> _uiState.update { currentState -> updateState(currentState, response) } }
    }
    private fun <R> runOnViewModelScope(
        block: suspend () -> R,
        updateState: (DepositUiState, R) -> DepositUiState
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
                return DepositViewModel(
                    application.walletRepository,
                    application.userRepository,
                    application.sessionManager
                ) as T
            }
        }
    }

}