package com.shellinfo.common.data.remote.response.model.payment_gateway.cash_free

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

@JsonClass(generateAdapter = true)
data class CashFreePaymentResponse(

    @Json(name ="order_id") val order_id: String,
    @Json(name ="cf_order_id") val cf_order_id: String,
    @Json(name ="entity") val entity: String,
    @Json(name ="order_currency") val order_currency: String,
    @Json(name ="order_amount") val order_amount: Double,
    @Json(name ="order_expiry_time") val order_expiry_time: String,
    @Json(name ="customer_details") val customer_details: CustomerDetails,
    @Json(name ="order_meta") val order_meta: OrderMeta,
    @Json(name ="settlements") val settlements: Settlements?=null,
    @Json(name ="payments") val payments: Payments?=null,
    @Json(name ="refunds") val refunds: Refunds?=null,
    @Json(name ="order_status") val order_status: String,
    @Json(name ="order_token") val order_token: String?=null,
    @Json(name ="payment_link") val payment_link: String?=null,
    @Json(name ="payment_session_id") val payment_session_id: String,
)


@JsonClass(generateAdapter = true)
data class Settlements(
    @Json(name ="url") val url: String,
)

@JsonClass(generateAdapter = true)
data class Payments(
    @Json(name ="url") val url: String,
)

@JsonClass(generateAdapter = true)
data class Refunds(
    @Json(name ="url") val url: String,
)
