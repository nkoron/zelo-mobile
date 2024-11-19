package com.example.zelo.network

import com.example.zelo.model.Balance
import com.example.zelo.model.BalanceRequest
import com.example.zelo.model.BalanceResponse
import com.example.zelo.model.Card
import com.example.zelo.model.EmailRequest
import com.example.zelo.model.LoginRequest
import com.example.zelo.model.LoginResponse
import com.example.zelo.model.Payment
import com.example.zelo.model.PaymentIdRequest
import com.example.zelo.model.PaymentRequest
import com.example.zelo.model.RegisterUser
import com.example.zelo.model.ResetPasswordRequest
import com.example.zelo.model.UpdateAliasRequest
import com.example.zelo.model.User
import com.example.zelo.model.VerificationCodeRequest
import com.example.zelo.model.WalletDetails
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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
    suspend fun getPaymentById(paymentIdRequest: PaymentIdRequest): Payment

    @GET("api/payment/link/{linkUUID}")
    suspend fun getPaymentsByLinkUUID(linkUUID: String): Payment

    @POST("api/payment")
    suspend fun createPayment(@Body paymentRequest: PaymentRequest): Payment

    @POST("api/payment/link/{linkUUID}")
    suspend fun createPaymentByLinkUUID(@Body paymentRequest: PaymentRequest, @Path("linkUUID") linkUUID: String): Payment

    @GET("api/user")
    suspend fun getUser(): User

    @POST("api/user/verify")
    suspend fun verifyUser(@Body verificationCodeRequest: VerificationCodeRequest): User

    @POST("api/user/recover-password")
    suspend fun recoverPassword(@Body emailRequest: EmailRequest): Int

    @POST("api/user")
    suspend fun registerUser(@Body user: RegisterUser): User

    @POST("api/user/login")
    suspend fun loginUser(@Body loginRequest: LoginRequest): LoginResponse //retorna un "token"

    @POST("api/user/logout")
    suspend fun logoutUser()

    @POST("api/user/reset-password")
    suspend fun resetPassword(@Body resetPasswordRequest: ResetPasswordRequest): Int //tenemos qeu manejar el si es error o si es una user

    @GET("api/wallet/balance")
    suspend fun getBalance(): Balance

    @GET("api/wallet/cards")
    suspend fun getCards(): List<Card>

    @GET("api/wallet/details")
    suspend fun getWalletDetails(): WalletDetails

    @POST("api/wallet/recharge")
    suspend fun rechargeWallet(@Body balanceRequest: BalanceRequest): BalanceResponse //retorna newBalance o un error

    @POST("api/wallet/cards")
    suspend fun addCard(@Body card: Card): Card

    @PUT("api/wallet/update-alias")
    suspend fun updateAlias(@Body updateAliasRequest: UpdateAliasRequest): WalletDetails


}







object WalletApi {
    val retrofitService: WalletApiService by lazy {
        retrofit.create(WalletApiService::class.java)

    }
}