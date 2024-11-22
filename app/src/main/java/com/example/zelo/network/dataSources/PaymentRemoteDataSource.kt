package com.example.zelo.network.dataSources

import android.util.Log
import com.example.zelo.network.WalletApiService
import com.example.zelo.network.model.LinkPayment
import com.example.zelo.network.model.Payment
import com.example.zelo.network.model.PaymentIdRequest
import com.example.zelo.network.model.PaymentRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PaymentRemoteDataSource(
    private val userService: WalletApiService,
    ) : RemoteDataSource() {

        suspend fun getPayments() : List<Payment> {
            val aux =  handleApiResponse {
                userService.getPayments()
            }
            return aux.payments
    }
    suspend fun makePayment(payment: PaymentRequest): Payment {
        Log.d("PaymentRemoteDataSource", "Making payment: $payment")
        return handleApiResponse {
            userService.createPayment(payment)
        }

    }
    suspend fun makeLinkPayment(payment: PaymentRequest): LinkPayment {
        Log.d("PaymentRemoteDataSource", "Making payment: $payment")
        return handleApiResponse {
            userService.createLinkPayment(payment)
        }

    }
    suspend fun getLinkDetails(linkUUID: String): Payment {
        return handleApiResponse {
            userService.getPaymentsByLinkUUID(linkUUID)
        }
    }
    suspend fun getPaymentById(paymentId: Int): Payment {
        return handleApiResponse {
            userService.getPaymentById(PaymentIdRequest(paymentId))
        }
    }
    suspend fun payPaymentByLinkUUID(payment: PaymentRequest, linkUUID: String): Payment {
        return handleApiResponse {
            userService.createPaymentByLinkUUID(payment, linkUUID)
        }
    }
    val paymentStream: Flow<List<Payment>> = flow {
        while (true) {
            val paymentDetails = handleApiResponse {
                userService.getPayments()
            }
            emit(paymentDetails.payments)
            delay(DELAY)
        }
    }

    companion object {
        const val DELAY: Long = 3000
    }
}