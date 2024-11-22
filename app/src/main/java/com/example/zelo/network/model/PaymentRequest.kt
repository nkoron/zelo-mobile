package com.example.zelo.network.model

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable(with = PaymentRequestSerializer::class)
sealed class PaymentRequest {
    abstract val type: String
}

@Serializable
data class BalancePaymentRequest(
    val receiverEmail: String?,
    val amount: Int,
    val description: String,
    override val type: String = "BALANCE"
) : PaymentRequest()

@Serializable
data class CardPaymentRequest(
    val cardId: Int,
    val receiverEmail: String?,
    val amount: Int?,
    val description: String,
    override val type: String = "CARD"
) : PaymentRequest()

@Serializable
data class CreateLinkPaymentRequest(
    val amount: Int,
    val description: String,
    override val type: String = "LINK"
) : PaymentRequest()

@Serializable
data class LinkPaymentRequest(
    val cardId: Int? = null,
    override val type: String
) : PaymentRequest()

@Serializable
data class PaymentIdRequest(
    val id: Int
)

object PaymentRequestSerializer : JsonContentPolymorphicSerializer<PaymentRequest>(PaymentRequest::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out PaymentRequest> {
        return when(JsonObject(element.jsonObject)["type"]?.jsonPrimitive?.content) {
            "BALANCE" -> BalancePaymentRequest.serializer()
            "CARD" -> CardPaymentRequest.serializer()
            "LINK" -> CreateLinkPaymentRequest.serializer()
            else -> throw SerializationException("Unknown payment type")
        }
    }
}