package com.example.zelo.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zelo.model.Balance
import com.example.zelo.model.Payment
import com.example.zelo.network.WalletApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface WalletUiState {
    data object Loading : WalletUiState
    data object Error : WalletUiState
    data class Success(val payments: List<Payment>, val balance: Balance) : WalletUiState
}

class WalletViewModel : ViewModel() {
    var walletUiState: WalletUiState by mutableStateOf(WalletUiState.Loading)
        private set
    init {
        getHomePageData()
    }

    fun getHomePageData() {
        viewModelScope.launch {
            walletUiState = WalletUiState.Loading

            walletUiState = try {
                coroutineScope {
                    // Ejecuta ambas llamadas en paralelo
                    val paymentsDeferred = async { WalletApi.retrofitService.getPayments() }
                    val balanceDeferred = async { WalletApi.retrofitService.getBalance() }

                    val payments = paymentsDeferred.await()
                    val balance = balanceDeferred.await()

                    // Actualiza el estado con los datos de ambas llamadas
                    WalletUiState.Success(payments, balance)
                }
            } catch (e: IOException) {
                WalletUiState.Error
            } catch (e: HttpException) {
                WalletUiState.Error
            }
        }
    }
}
