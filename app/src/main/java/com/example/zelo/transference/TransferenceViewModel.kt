package com.example.zelo.transference

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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TransferenceUiState(
    val isFetching: Boolean = false,
    val movements: List<Payment> = emptyList(),
    val user: User? = null,
    val error: Error? = null,
    val isAuthenticated: Boolean = false
)

class TransferenceViewModel(
    private val paymentRepository: PaymentRepository,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private var paymentsStreamJob: Job? = null
    private val _uiState = MutableStateFlow(TransferenceUiState())
    val uiState: StateFlow<TransferenceUiState> = _uiState.asStateFlow()

    init {
        observeLogoutSignal()
        checkAuthenticationStatus()
    }

    fun checkAuthenticationStatus() {
        val isAuthenticated = sessionManager.loadAuthToken() != null
        _uiState.update { it.copy(isAuthenticated = isAuthenticated) }
        if (isAuthenticated) {
            getCurrentUser()
            observePaymentStream()
        } else {
            _uiState.update { it.copy(user = null, movements = emptyList()) }
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isFetching = true, error = null) }
            try {
                val user = userRepository.getCurrentUser(refresh = true)
                _uiState.update { it.copy(user = user, isAuthenticated = true, isFetching = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = handleError(e), isFetching = false) }
            }
        }
    }

    private fun observeLogoutSignal() {
        viewModelScope.launch {
            sessionManager.logoutSignal.collect {
                paymentsStreamJob?.cancel()
                _uiState.update { it.copy(movements = emptyList(), user = null, isAuthenticated = false) }
            }
        }
    }

    private fun observePaymentStream() {
        paymentsStreamJob?.cancel()
        paymentsStreamJob = viewModelScope.launch {
            combine(
                paymentRepository.paymentStream,
                _uiState.map { it.user }
            ) { payments, user ->
                payments.filter { it.payer?.id == user?.id }
            }
                .distinctUntilChanged()
                .catch { e -> _uiState.update { it.copy(error = handleError(e)) } }
                .collect { totalPayments ->
                    _uiState.update { it.copy(movements = totalPayments) }
                }
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
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TransferenceViewModel(
                    application.paymentRepository,
                    application.userRepository,
                    application.sessionManager
                ) as T
            }
        }
    }
}

