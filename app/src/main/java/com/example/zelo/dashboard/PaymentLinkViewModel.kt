import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.zelo.MyApplication
import com.example.zelo.network.model.CreateLinkPaymentRequest
import com.example.zelo.network.repository.PaymentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

data class PaymentLinkUiState(
    val amount: String = "",
    val description: String = "",
    val generatedLinkUuid: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class PaymentLinkViewModel(
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentLinkUiState())
    val uiState: StateFlow<PaymentLinkUiState> = _uiState.asStateFlow()

    fun updateAmount(amount: String) {
        _uiState.update { it.copy(amount = amount) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }
    val invalidAmountError = if (Locale.getDefault().language == "es") "Monto inválido" else "Invalid amount"
    val errorMessage = if (Locale.getDefault().language == "es") "Error al generar el enlace de pago" else "Error generating payment link"

    fun generatePaymentLink() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val amount = _uiState.value.amount.toDoubleOrNull()
                if (amount == null) {
                    _uiState.update { it.copy(isLoading = false, error = invalidAmountError) }
                    return@launch
                }

                val linkPaymentRequest = CreateLinkPaymentRequest(
                    amount = amount,
                    description = _uiState.value.description
                )

                val result = paymentRepository.createPayLink(linkPaymentRequest)
                _uiState.update { it.copy(isLoading = false, generatedLinkUuid = result.linkUuid) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: errorMessage) }
            }
        }
    }

    fun resetState() {
        _uiState.update { PaymentLinkUiState() }
    }

    companion object {
        fun provideFactory(
            application: MyApplication
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PaymentLinkViewModel(
                    application.paymentRepository
                ) as T
            }
        }
    }
}

