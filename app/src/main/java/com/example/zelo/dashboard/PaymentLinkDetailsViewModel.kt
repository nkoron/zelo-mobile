import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.zelo.MyApplication
import com.example.zelo.network.model.*
import com.example.zelo.network.repository.PaymentRepository
import com.example.zelo.network.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

enum class PaymentMethod {
    BALANCE, CARD
}

data class PaymentLinkDetailsUiState(
    val isLoading: Boolean = false,
    val paymentDetails: Payment? = null,
    val balance: Balance? = null,
    val cards: List<Card> = emptyList(),
    val selectedPaymentMethod: PaymentMethod? = null,
    val selectedCardId: Int? = null,
    val error: String? = null,
    val paymentComplete: Boolean = false
)

class PaymentLinkDetailsViewModel(
    private val paymentRepository: PaymentRepository,
    private val walletRepository: WalletRepository,
    private val linkUuid: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentLinkDetailsUiState())
    val uiState: StateFlow<PaymentLinkDetailsUiState> = _uiState.asStateFlow()

    init {
        loadLinkDetails(linkUuid)
        loadBalance()
        loadCards()
    }
    val invalidAmountError = if (Locale.getDefault().language == "es") "Error desconocido" else "Unknown error"


    fun loadLinkDetails(linkUuid: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val details = paymentRepository.getLinkDetails(linkUuid)
                _uiState.update { it.copy(isLoading = false, paymentDetails = details) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: invalidAmountError) }
            }
        }
    }

    private fun loadBalance() {
        viewModelScope.launch {
            try {
                val balance = walletRepository.getBalance()
                _uiState.update { it.copy(balance = balance) }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun loadCards() {
        viewModelScope.launch {
            try {
                val cards = walletRepository.getCards()
                _uiState.update { it.copy(cards = cards) }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun selectPaymentMethod(method: PaymentMethod, cardId: Int? = null) {
        _uiState.update {
            it.copy(
                selectedPaymentMethod = method,
                selectedCardId = if (method == PaymentMethod.CARD) cardId else null
            )
        }
    }
    val invalidMethodError = if (Locale.getDefault().language == "es") "Ningún método de pago seleccionado" else "No payment method selected"

    fun processPayment() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val paymentRequest = when (uiState.value.selectedPaymentMethod) {
                    PaymentMethod.BALANCE -> {
                        LinkPaymentRequest(
                            type = "BALANCE",
                        )
                    }
                    PaymentMethod.CARD -> {
                        LinkPaymentRequest(
                            cardId = uiState.value.selectedCardId ?: 0,
                            type = "CARD"
                        )}
                    null -> throw IllegalStateException(invalidMethodError)
                }

                val result = paymentRepository.payPaymentByLinkUUID(paymentRequest, linkUuid)
                _uiState.update { it.copy(isLoading = false, paymentComplete = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: invalidAmountError) }
            }
        }
    }

    companion object {
        fun provideFactory(
            application: MyApplication,
            linkUuid: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PaymentLinkDetailsViewModel(
                    application.paymentRepository,
                    application.walletRepository,
                    linkUuid
                ) as T
            }
        }
    }
}