package com.example.zelo.network.dataSources

import com.example.zelo.network.WalletApiService
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
            return handleApiResponse {
                userService.getPayments()
            }
    }
    suspend fun makePayment(payment: PaymentRequest): Payment {
        return handleApiResponse {
            userService.createPayment(payment)
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
            emit(paymentDetails)
            delay(DELAY)
        }
    }

    companion object {
        const val DELAY: Long = 3000
    }
}