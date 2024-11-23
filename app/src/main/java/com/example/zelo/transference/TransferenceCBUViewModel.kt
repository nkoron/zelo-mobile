package com.example.zelo.transference

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.zelo.MyApplication
import com.example.zelo.cards.inferBankName
import com.example.zelo.network.SessionManager
import com.example.zelo.network.dataSources.DataSourceException
import com.example.zelo.network.model.*
import com.example.zelo.network.repository.PaymentRepository
import com.example.zelo.network.repository.WalletRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

data class TransferenceCBUUiState(
    val isLoading: Boolean = false,
    val email: String = "",
    val amount: String = "",
    val concept: String = "",
    val selectedPaymentMethod: PaymentMethod? = null,
    val availablePaymentMethods: List<PaymentMethod> = emptyList(),
    val error: Error? = null,
    val transferSuccess: Boolean = false,
)

data class PaymentMethod(
    val id: Int? = null,
    val type: String,
    val name: String,
    val digits: String,
    val balance: String? = null,
    val cardType: String? = null,
    val backgroundColor: Color = Color(0xFFF5F5F5)
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
//        observeLogoutSignal()
    }

    private fun loadPaymentMethods() = runOnViewModelScope(
        {
            val walletDetails = walletRepository.walletDetailStream.first()
            val cards = walletRepository.cardsStream.first()
            createPaymentMethods(walletDetails, cards)
        },
        { state, paymentMethods -> state.copy(availablePaymentMethods = paymentMethods, selectedPaymentMethod = paymentMethods.first()) }
    )
//    private fun observeLogoutSignal(){
//        viewModelScope.launch {
//            sessionManager.logoutSignal.collect {
//                walletRepository.cardsStream?.cancel()
//            }
//        }    }

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

        cards.forEach { card ->
            paymentMethods.add(
                PaymentMethod(
                    id = card.id,
                    type = "CREDIT",
                    name = inferBankName(card.number),
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

    val invalidPaymentMethod = if (Locale.getDefault().language == "es") "Método de pago seleccionado inválido" else "Invalid payment method selected"


    fun makeTransfer() = runOnViewModelScope(
        {
            val currentState = _uiState.value
            val amount = currentState.amount.toDoubleOrNull() ?: 0.0
            val paymentRequest: PaymentRequest = when (currentState.selectedPaymentMethod?.type) {
                "BALANCE" -> BalancePaymentRequest(
                    receiverEmail = currentState.email,
                    amount = amount,
                    description = currentState.concept.ifBlank { "-" },
                    type = "BALANCE"
                )
                "CREDIT" -> CardPaymentRequest(
                    cardId = currentState.selectedPaymentMethod.id?.toInt() ?: 0,
                    receiverEmail = currentState.email,
                    amount = amount,
                    description = currentState.concept.ifBlank { "-" },
                    type = "CARD"
                )
                else -> throw IllegalStateException(invalidPaymentMethod)
            }
            paymentRepository.makePayment(paymentRequest)
        },
        { state, _ -> state.copy(transferSuccess = true) }
    )

    fun resetTransferForm() {
        _uiState.update { it.copy(
            email = "",
            amount = "",
            concept = "",
            selectedPaymentMethod = null
        ) }
    }

    private fun <R> runOnViewModelScope(
        block: suspend () -> R,
        updateState: (TransferenceCBUUiState, R) -> TransferenceCBUUiState
    ) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = null) }
        runCatching {
            block()
        }.onSuccess { response ->
            _uiState.update { currentState -> updateState(currentState, response).copy(isLoading = false) }
        }.onFailure { e ->
            _uiState.update { it.copy(isLoading = false, error = handleError(e)) }
        }
    }

    private fun handleError(e: Throwable): Error {
        return if (e is DataSourceException) {
            Error(e.code, e.message ?: "")
        } else {
            Error(null, e.message ?: "")
        }
    }

    fun refreshPaymentMethods() {
        loadPaymentMethods()
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

