package com.example.zelo.network

import com.example.zelo.model.Balance
import com.example.zelo.model.Card
import com.example.zelo.model.Payment
import com.example.zelo.model.User
import com.example.zelo.model.WalletDetails
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET

private const val BASE_URL= "localhost:8080/"

private val httpLoggingInterceptor = HttpLoggingInterceptor()
    .setLevel(HttpLoggingInterceptor.Level.BODY)

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(httpLoggingInterceptor)
    .build()

private val json = Json {ignoreUnknownKeys = true}

private val retrofit = Retrofit.Builder()
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .build()

interface WalletApiService {
@GET("api/payment")
suspend fun getPayments(): List<Payment>

@GET("api/payment/{paymentId}")
suspend fun getPaymentById(paymentId: Int): Payment

@GET("api/payment/link/{linkUUID}")
suspend fun getPaymentsByLinkUUID(linkUUID: String): Payment

@GET("api/user")
suspend fun getUser(): User

@GET("api/wallet/balance")
suspend fun getBalance(): Balance

@GET("api/wallet/cards")
suspend fun getCards(): List<Card>

@GET("api/wallet/details")
suspend fun getWalletDetails(): WalletDetails

}

object WalletApi {
    val retrofitService: WalletApiService by lazy {
        retrofit.create(WalletApiService::class.java)

    }
}