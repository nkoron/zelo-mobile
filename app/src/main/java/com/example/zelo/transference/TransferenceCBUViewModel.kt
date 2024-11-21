package com.example.zelo.transference

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.zelo.MyApplication
import com.example.zelo.network.SessionManager
import com.example.zelo.network.dataSources.DataSourceException
import com.example.zelo.network.model.*
import com.example.zelo.network.repository.PaymentRepository
import com.example.zelo.network.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.util.Log

data class TransferenceCBUUiState(
    val isLoading: Boolean = false,
    val cbuAlias: String = "",
    val amount: String = "",
    val concept: String = "",
    val selectedPaymentMethod: PaymentMethod? = null,
    val availablePaymentMethods: List<PaymentMethod> = emptyList(),
    val error: Error? = null,
    val transferSuccess: Boolean = false,
)


class TransferenceCBUViewModel(
    private val walletRepository: WalletRepository,
    private val paymentRepository: PaymentRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransferenceCBUUiState())
    val uiState: StateFlow<TransferenceCBUUiState> = _uiState.asStateFlow()

    init {
        loadPaymentMethods()
    }

    private fun loadPaymentMethods() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val cards = walletRepository.getCards()
                val walletDetails = walletRepository
                val paymentMethods = mutableListOf<PaymentMethod>()

                // Add wallet balance as a payment method
                paymentMethods.add(
                    PaymentMethod(
                        type = "BALANCE",
                        name = "Saldo en Cuenta",
                        lastDigits = "",
                        balance = walletDetails.getBalance().balance.toString(),
                        backgroundColor = Color(0xFFF5F5F5)
                    )
                )

                // Add cards as payment methods
                cards.forEach { card ->
                    paymentMethods.add(
                        PaymentMethod(
                            id = card.id,
                            type = "CREDIT",
                            name = card.type,
                            lastDigits = card.number.takeLast(4),
                            cardType = "Tarjeta de Crédito",
                            backgroundColor = Color(0xFF6C63FF)
                        )
                    )
                }

                _uiState.update { it.copy(availablePaymentMethods = paymentMethods, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = handleError(e), isLoading = false) }
            }
        }
    }

    fun updateCbuAlias(cbuAlias: String) {
        _uiState.update { it.copy(cbuAlias = cbuAlias) }
    }

    fun updateAmount(amount: String) {
        _uiState.update { it.copy(amount = amount) }
    }

    fun updateConcept(concept: String) {
        _uiState.update { it.copy(concept = concept) }
    }

    fun selectPaymentMethod(paymentMethod: PaymentMethod?) {
        _uiState.update { it.copy(selectedPaymentMethod = paymentMethod) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun makeTransfer() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val currentState = _uiState.value
                val amount = currentState.amount.toIntOrNull() ?: 0
                val paymentRequest: PaymentRequest = when (currentState.selectedPaymentMethod?.type) {
                    "BALANCE" -> BalancePaymentRequest(
                        receiverEmail = currentState.cbuAlias,
                        amount = amount,
                        description = currentState.concept,
                        type = "BALANCE"
                    )
                    "CREDIT" -> CardPaymentRequest(
                        cardId = currentState.selectedPaymentMethod.id?.toInt() ?: 0,
                        receiverEmail = currentState.cbuAlias,
                        amount = amount,
                        description = currentState.concept,
                        type = "CARD"
                    )
                    else -> throw IllegalStateException("Invalid payment method selected")
                }
                val result = paymentRepository.makePayment(paymentRequest)
                _uiState.update { it.copy(transferSuccess = true, isLoading = false) }
                Log.d("TransferenceCBUViewModel", "Transfer successful: $result")
                updateCbuAlias("")
                updateAmount("")
                updateConcept("")
                selectPaymentMethod(null)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = handleError(e), isLoading = false) }
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
                return TransferenceCBUViewModel(
                    application.walletRepository,
                    application.paymentRepository,
                    application.sessionManager
                ) as T
            }
        }
    }
}