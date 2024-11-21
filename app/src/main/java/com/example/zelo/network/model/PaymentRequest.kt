package com.example.zelo.network.model

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*

@Serializable(with = PaymentRequestSerializer::class)
sealed class PaymentRequest {
    abstract val receiverEmail: String?
    abstract val amount: Int?
    abstract val description: String
    abstract val type: String
}

@Serializable
data class BalancePaymentRequest(
    override val receiverEmail: String?,
    override val amount: Int,
    override val description: String,
    override val type: String = "BALANCE"
) : PaymentRequest()

@Serializable
data class CardPaymentRequest(
    val cardId: Int,
    override val receiverEmail: String?,
    override val amount: Int?,
    override val description: String,
    override val type: String = "CARD"
) : PaymentRequest()

@Serializable
data class LinkPaymentRequest(
    override val amount: Int,
    override val description: String,
    override val receiverEmail: String?,
    override val type: String = "LINK"
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
            "LINK" -> LinkPaymentRequest.serializer()
            else -> throw SerializationException("Unknown payment type")
        }
    }
}