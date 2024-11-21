package com.example.zelo.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.zelo.MyApplication
import com.example.zelo.network.SessionManager
import com.example.zelo.network.dataSources.DataSourceException
import com.example.zelo.network.model.Error
import com.example.zelo.network.model.Payment
import com.example.zelo.network.model.User
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


data class IncomeUiState (
    val isFetching: Boolean = false,
    val movements: List<Payment> = emptyList(),
    val user: User? = null,
    val error: Error? = null
)

class IncomeViewModel(
    private val paymentRepository: PaymentRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private var expensesStreamJob: Job? = null
    private val _uiState = MutableStateFlow(IncomeUiState())
    val uiState: StateFlow<IncomeUiState> = _uiState.asStateFlow()

    init {
        getCurrentUser()
        observeExpensesStream()
    }
    fun getCurrentUser()= runOnViewModelScope(
        block = { userRepository.getCurrentUser() },
        updateState = { state, response -> state.copy(user = response) }
    )

    private fun observeExpensesStream() {
        expensesStreamJob = collectOnViewModelScope(
            paymentRepository.paymentStream
        ) { state, response -> state.copy(movements = response.filter { it.receiver.id == uiState.value.user?.id }) }
    }

    private fun <T> collectOnViewModelScope(
        flow: Flow<T>,
        updateState: (IncomeUiState, T) -> IncomeUiState
    ) = viewModelScope.launch {
        flow
            .distinctUntilChanged()
            .catch { e -> _uiState.update { currentState -> currentState.copy(error = handleError(e)) } }
            .collect { response -> _uiState.update { currentState -> updateState(currentState, response) } }
    }
    private fun <R> runOnViewModelScope(
        block: suspend () -> R,
        updateState: (IncomeUiState, R) -> IncomeUiState
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
                return IncomeViewModel(
                    application.paymentRepository,
                    application.userRepository
                ) as T
            }
        }
    }

}