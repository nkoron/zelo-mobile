package com.example.zelo.network

import com.example.zelo.network.model.Balance
import com.example.zelo.network.model.BalanceRequest
import com.example.zelo.network.model.BalanceResponse
import com.example.zelo.network.model.Card
import com.example.zelo.network.model.EmailRequest
import com.example.zelo.network.model.LoginRequest
import com.example.zelo.network.model.LoginResponse
import com.example.zelo.network.model.Payment
import com.example.zelo.network.model.PaymentIdRequest
import com.example.zelo.network.model.PaymentRequest
import com.example.zelo.network.model.RegisterResponse
import com.example.zelo.network.model.RegisterUser
import com.example.zelo.network.model.ResetPasswordRequest
import com.example.zelo.network.model.UpdateAliasRequest
import com.example.zelo.network.model.User
import com.example.zelo.network.model.VerificationCodeRequest
import com.example.zelo.network.model.WalletDetails
import com.example.zelo.network.model.getCardsResponse
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface WalletApiService {
    @GET("api/payment")
    suspend fun getPayments(): Response<List<Payment>>

    @GET("api/payment/{paymentId}")
    suspend fun getPaymentById(paymentIdRequest: PaymentIdRequest): Response<Payment>

    @GET("api/payment/link/{linkUUID}")
    suspend fun getPaymentsByLinkUUID(linkUUID: String): Response<Payment>

    @POST("api/payment")
    suspend fun createPayment(@Body paymentRequest: PaymentRequest): Response<Payment>

    @POST("api/payment/link/{linkUUID}")
    suspend fun createPaymentByLinkUUID(@Body paymentRequest: PaymentRequest, @Path("linkUUID") linkUUID: String): Response<Payment>

    @GET("api/user")
    suspend fun getUser(): Response<User>

    @POST("api/user/verify")
    suspend fun verifyUser(@Body verificationCodeRequest: VerificationCodeRequest): Response<RegisterResponse>

    @POST("api/user/recover-password")
    suspend fun recoverPassword(@Body emailRequest: EmailRequest): Response<Int>

    @POST("api/user")
    suspend fun registerUser(@Body user: RegisterUser): Response<RegisterResponse>

    @POST("api/user/login")
    suspend fun loginUser(@Body loginRequest: LoginRequest): Response<LoginResponse> //retorna un "token"

    @POST("api/user/logout")
    suspend fun logoutUser(): Response<Unit>

    @POST("api/user/reset-password")
    suspend fun resetPassword(@Body resetPasswordRequest: ResetPasswordRequest): Response<Int>//tenemos qeu manejar el si es error o si es una user

    @GET("api/wallet/balance")
    suspend fun getBalance(): Response<Balance>

    @GET("api/wallet/cards")
    suspend fun getCards(): Response<getCardsResponse>

    @GET("api/wallet/details")
    suspend fun getWalletDetails(): Response<WalletDetails>

    @POST("api/wallet/recharge")
    suspend fun rechargeWallet(@Body balanceRequest: BalanceRequest): Response<BalanceResponse> //retorna newBalance o un error

    @POST("api/wallet/cards")
    suspend fun addCard(@Body card: Card): Response<Card>

    @PUT("api/wallet/update-alias")
    suspend fun updateAlias(@Body updateAliasRequest: UpdateAliasRequest): Response<WalletDetails>


    @DELETE("api/wallet/cards/{cardId}")
    suspend fun deleteCard(@Path("cardId") cardId: Int): Response<Unit>

}