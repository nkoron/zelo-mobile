package com.example.zelo.network.repository

import android.util.Log
import com.example.zelo.network.dataSources.PaymentRemoteDataSource
import com.example.zelo.network.model.Card
import com.example.zelo.network.model.LinkPaymentRequest
import com.example.zelo.network.model.Payment
import com.example.zelo.network.model.PaymentRequest
import com.example.zelo.network.model.WalletDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PaymentRepository(
    private val dataSource: PaymentRemoteDataSource
) {
    private val payMutex = Mutex()

    private var payments: List<Payment> = emptyList()

    suspend fun getPayments(refresh: Boolean = false): List<Payment> {
        if(refresh || payments.isEmpty()){
            val result =  dataSource.getPayments()

            payMutex.withLock {
                this.payments = result
            }
        }
        return payMutex.withLock { this.payments }
    }
    suspend fun makePayment(payment: PaymentRequest): Payment {
        Log.d("PaymentRepository", "Making payment: $payment")
        val newCard =  dataSource.makePayment(payment)
        payMutex.withLock {
            this.payments = emptyList()
        }
        return newCard
    }
    suspend fun createPayLink(payment: LinkPaymentRequest) {
        dataSource.makePayment(payment)
        payMutex.withLock {
            this.payments = emptyList()
        }
    }
    suspend fun getLinkDetails(linkUUID: String): Payment {
        return dataSource.getLinkDetails(linkUUID)
    }
    suspend fun getPaymentById(paymentId: Int): Payment {
        return dataSource.getPaymentById(paymentId)
        }
    suspend fun payPaymentByLinkUUID(payment: LinkPaymentRequest, linkUUID: String): Payment {
        return dataSource.payPaymentByLinkUUID(payment, linkUUID)
    }
    suspend fun getPaymentsByType(type: String): List<Payment> {
        if(type != "CARD" && type != "BALANCE" && type != "LINK")
            throw IllegalArgumentException("Invalid payment type")
        return dataSource.getPayments().filter { it.type == type }
    }

    val paymentStream: Flow<List<Payment>> =
        dataSource.paymentStream
}