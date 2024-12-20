package com.example.zelo.login_register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.zelo.MyApplication
import com.example.zelo.network.SessionManager
import com.example.zelo.network.dataSources.DataSourceException
import com.example.zelo.network.model.Error
import com.example.zelo.network.model.RegisterUser
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
import java.util.Locale


data class AuthUiState (
    val isAuthenticated: Boolean = false,
    val user: User? = null,
    val isFetching: Boolean = false,
    val walletDetail: WalletDetails? = null,
    val error: Error? = null,
    val isResetLinkSent: Boolean = false
)

class AuthViewModel(
    private val walletRepository: WalletRepository,
    private val sessionManager: SessionManager,
    private val userRepository: UserRepository,
    private val paymentRepository: PaymentRepository
) : ViewModel() {


    private var walletDetailStreamJob: Job? = null
    private val _uiState = MutableStateFlow(AuthUiState(isAuthenticated = sessionManager.loadAuthToken() != null))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        observeLogoutSignal()
        if (uiState.value.isAuthenticated) {
            observeWalletDetailStream()
            getCurrentUser()
        }
    }

    fun login(username: String, password: String) = runOnViewModelScope(
        {
            userRepository.login(username, password)
            userRepository.getCurrentUser()
            observeWalletDetailStream()
        },
        { state, _ -> state.copy(isAuthenticated = true) }
    )

    fun logout() = runOnViewModelScope(
        {
            walletDetailStreamJob?.cancel()
            paymentRepository.logout()
            walletRepository.logout()
            userRepository.logout()
            sessionManager.logout()
        },
        { state, _ ->
            state.copy(
                isAuthenticated = false,
            walletDetail = null, // Reinicia datos de wallet
            user = null,         // Reinicia datos de usuario
            isResetLinkSent = false,
            error = null         // Limpia errores previos
            )
        }
    )

    fun getCurrentUser() = runOnViewModelScope(
        {
            userRepository.getCurrentUser()
        },
        { state, response -> state.copy(user = response) }
    )
    fun registerUser(user: RegisterUser) = runOnViewModelScope(
        {
            userRepository.registerUser(user)
        },
        { state, response -> state.copy(user = response) }
    )
    fun verifyUser(token: String) = runOnViewModelScope(
        {
            userRepository.verifyUser(token)
        },
        { state, response -> state.copy(user = response) }
    )

    private fun observeWalletDetailStream() {
        walletDetailStreamJob?.cancel()
        walletDetailStreamJob = collectOnViewModelScope(
            walletRepository.walletDetailStream
        ) { state, response -> state.copy(walletDetail = response) }
    }

    private fun <T> collectOnViewModelScope(
        flow: Flow<T>,
        updateState: (AuthUiState, T) -> AuthUiState
    ) = viewModelScope.launch {
        flow
            .distinctUntilChanged()
            .catch { e -> _uiState.update { currentState -> currentState.copy(error = handleError(e)) } }
            .collect { response -> _uiState.update { currentState -> updateState(currentState, response) } }
    }
    private fun <R> runOnViewModelScope(
        block: suspend () -> R,
        updateState: (AuthUiState, R) -> AuthUiState
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
    private fun observeLogoutSignal(){
        viewModelScope.launch {
            sessionManager.logoutSignal.collect {
                walletDetailStreamJob?.cancel()
                _uiState.update { c ->c.copy(walletDetail = null, user = null) }
            }
        }
    }

    companion object {
        fun provideFactory(
            application: MyApplication
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun<T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(
                    application.walletRepository,
                    application.sessionManager,
                    application.userRepository,
                    application.paymentRepository
                ) as T
            }
        }
    }

    val InvalidError = if (Locale.getDefault().language == "es") "Fallo al enviar link de reestablecimiento" else "Failed to send reset link"


    fun recoverPassword(email: String) = viewModelScope.launch {
        _uiState.update { it.copy(isFetching = true, error = null, isResetLinkSent = false) }
        try {
            userRepository.recoverPassword(email)
            _uiState.update { it.copy(isFetching = false, isResetLinkSent = true) }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = Error(null, e.message ?: InvalidError), isFetching = false) }
        }
    }

    val FailedPasswordError = if (Locale.getDefault().language == "es") "Fallo al reestablecer contraseña" else "Failed to reset password"


    fun resetPassword(token: String, newPassword: String) = viewModelScope.launch {
        _uiState.update { it.copy(isFetching = true, error = null) }
        try {
            userRepository.resetPassword(token, newPassword)
            _uiState.update { it.copy(isAuthenticated = false, isFetching = false) }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = Error(null, e.message ?: FailedPasswordError), isFetching = false) }
        }
    }


}
