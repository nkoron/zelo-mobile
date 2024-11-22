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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import android.util.Log
import com.example.zelo.cards.inferBankName
import kotlinx.coroutines.Job
import java.util.Locale

data class TransferenceCBUUiState(
    val isLoading: Boolean = false,
    val email: String = "",  // Changed from cbuAlias to email
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

    private var paymentMethodsStreamJob: Job? = null

    init {
        observePaymentMethodsStream()
    }

    private fun observePaymentMethodsStream() {
        paymentMethodsStreamJob = viewModelScope.launch {
            combine(
                walletRepository.walletDetailStream,
                walletRepository.cardsStream
            ) { walletDetails, cards ->
                createPaymentMethods(walletDetails, cards)
            }
                .distinctUntilChanged()
                .catch { e -> _uiState.update { it.copy(error = handleError(e)) } }
                .collect { paymentMethods ->
                    _uiState.update { it.copy(availablePaymentMethods = paymentMethods, isLoading = false) }
                }
        }
    }

    private fun createPaymentMethods(walletDetails: WalletDetails, cards: List<Card>): List<PaymentMethod> {
        val paymentMethods = mutableListOf<PaymentMethod>()

        val language = Locale.getDefault().language
        val name = if (language == "es") "Saldo en Cuenta" else "Account Balance"

        paymentMethods.add(
            PaymentMethod(
                type = "BALANCE",
                name = name,
                digits = "",
                balance = walletDetails.balance.toString(),
                backgroundColor = Color(0xFFF5F5F5)
            )
        )

        // Add cards as payment methods
        cards.forEach { card ->
            paymentMethods.add(
                PaymentMethod(
                    id = card.id,
                    type = "CREDIT",
                    name = inferBankName( card.number),
                    digits = card.number,
                    cardType = "Tarjeta de Crédito",
                    backgroundColor = Color(0xFF6C63FF)
                )
            )
        }

        return paymentMethods
    }


    fun updateAmount(amount: String) {
        _uiState.update { it.copy(amount = amount) }
    }

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email) }
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
                        receiverEmail = currentState.email,  // Changed to email
                        amount = amount,
                        description = currentState.concept,
                        type = "BALANCE"
                    )
                    "CREDIT" -> CardPaymentRequest(
                        cardId = currentState.selectedPaymentMethod.id?.toInt() ?: 0,
                        receiverEmail = currentState.email,  // Changed to email
                        amount = amount,
                        description = currentState.concept,
                        type = "CARD"
                    )
                    else -> throw IllegalStateException("Invalid payment method selected")
                }
                val result = paymentRepository.makePayment(paymentRequest)
                _uiState.update { it.copy(transferSuccess = true, isLoading = false) }
                Log.d("TransferenceCBUViewModel", "Transfer successful: $result")
                resetTransferForm()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = handleError(e), isLoading = false) }
            }
        }
    }


    private fun resetTransferForm() {
        _uiState.update { it.copy(
            email = "",
            amount = "",
            concept = "",
            selectedPaymentMethod = null
        ) }
    }

    private fun handleError(e: Throwable): Error {
        return if (e is DataSourceException) {
            Error(e.code, e.message ?: "")
        } else {
            Error(null, e.message ?: "")
        }
    }

    override fun onCleared() {
        super.onCleared()
        paymentMethodsStreamJob?.cancel()
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

