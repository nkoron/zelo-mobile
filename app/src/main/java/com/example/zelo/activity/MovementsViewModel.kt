package com.example.zelo.activity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.zelo.MyApplication
import com.example.zelo.network.SessionManager
import com.example.zelo.network.dataSources.DataSourceException
import com.example.zelo.network.model.Error
import com.example.zelo.network.model.Payment
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class MovementsUiState (
    val isFetching: Boolean = false,
    val movements: List<Payment> = emptyList(),
    val totalIncome: Double? = 0.0,
    val totalExpense: Double? = 0.0,
    val user: User? = null,
    val error: Error? = null
)

class MovementsViewModel(
    private val paymentRepository: PaymentRepository,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private var paymentStreamJob: Job? = null
    private var incomeStreamJob: Job? = null
    private var expenseStreamJob: Job? = null
    private val _uiState = MutableStateFlow(MovementsUiState())
    val uiState: StateFlow<MovementsUiState> = _uiState.asStateFlow()

    init {
        observeIncomeStream()
        observeExpenseStream()
        observePaymentStream()
        observeLogoutSignal()
        getCurrentUser()
    }
    fun getCurrentUser()= runOnViewModelScope(
            block = { userRepository.getCurrentUser() },
            updateState = { state, response -> state.copy(user = response) }
        )
    private fun observeLogoutSignal() {
        viewModelScope.launch {
            sessionManager.logoutSignal.collect {
                paymentStreamJob?.cancel()
                _uiState.update { c -> c.copy(movements = emptyList())  }
            }
        }
    }

    private fun observePaymentStream() {
        paymentStreamJob?.cancel()
        paymentStreamJob = viewModelScope.launch {
            paymentRepository.paymentStream
                .distinctUntilChanged()
                .catch { e -> _uiState.update { currentState -> currentState.copy(error = handleError(e)) } }
                .collect { payments ->
                    _uiState.update { currentState ->
                        Log.d("MovementsViewModel", "Payments: $payments")
                        currentState.copy(movements = payments.toList())
                    }
                }
        }
    }

    private fun observeIncomeStream() {
        incomeStreamJob?.cancel()
        incomeStreamJob = viewModelScope.launch {
            combine(
                paymentRepository.paymentStream,
                _uiState.map { it.user }
            ) { payments, user ->
                payments.filter { it.receiver.id == user?.id }.sumOf { it.amount }
            }
                .distinctUntilChanged()
                .catch { e -> _uiState.update { currentState -> currentState.copy(error = handleError(e)) } }
                .collect { totalIncome ->
                    _uiState.update { currentState -> currentState.copy(totalIncome = totalIncome) }
                }
        }
    }

    private fun observeExpenseStream() {
        expenseStreamJob?.cancel()
        expenseStreamJob = viewModelScope.launch {
            combine(
                paymentRepository.paymentStream,
                _uiState.map { it.user }
            ) { payments, user ->
                payments.filter { it.payer != null && it.payer.id == user?.id }.sumOf { it.amount }
            }
                .distinctUntilChanged()
                .catch { e -> _uiState.update { currentState -> currentState.copy(error = handleError(e)) } }
                .collect { totalExpense ->
                    _uiState.update { currentState -> currentState.copy(totalExpense = totalExpense) }
                }
        }
    }

    private fun <T> collectOnViewModelScope(
        flow: Flow<T>,
        updateState: (MovementsUiState, T) -> MovementsUiState
    ) = viewModelScope.launch {
        flow
            .catch { e -> _uiState.update { currentState -> currentState.copy(error = handleError(e)) } }
            .collect { response -> _uiState.update { currentState -> updateState(currentState, response) } }
    }

    private fun <R> runOnViewModelScope(
        block: suspend () -> R,
        updateState: (MovementsUiState, R) -> MovementsUiState
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
                return MovementsViewModel(
                    application.paymentRepository,
                    application.userRepository,
                    application.sessionManager
                ) as T
            }
        }
    }

}
