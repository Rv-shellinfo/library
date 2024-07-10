package com.shellinfo.common.data.remote.response.model.payment_gateway.cash_free

import com.shellinfo.common.code.enums.PaymentGatewayType
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CashFreePaymentRequest(

    @Json(name ="order_amount") val order_amount: String,
    @Json(name ="order_id") val order_id: String,
    @Json(name ="order_currency") val order_currency: String,
    @Json(name ="customer_details") val customer_details: CustomerDetails,
    @Json(name ="order_meta") val order_meta: OrderMeta,
    @Json(name ="order_note") val order_note: String
)


@JsonClass(generateAdapter = true)
data class CustomerDetails(
    @Json(name ="customer_id") val customer_id: String,
    @Json(name ="customer_name") val customer_name: String,
    @Json(name ="customer_email") val customer_email: String,
    @Json(name ="customer_phone") val customer_phone: String
)


@JsonClass(generateAdapter = true)
data class OrderMeta(
    @Json(name ="return_url") val notify_url: String
)
