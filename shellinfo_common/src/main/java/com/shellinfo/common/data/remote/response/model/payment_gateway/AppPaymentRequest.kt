package com.shellinfo.common.data.remote.response.model.payment_gateway

import com.shellinfo.common.code.enums.PaymentGatewayType
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AppPaymentRequest(

    @Json(name ="paymentGateway") var paymentGateway: PaymentGatewayType,
    @Json(name ="appVersionName") var appVersionName: String,
    @Json(name ="fareQuoteId") var fareQuoteId: String,
    @Json(name ="customer_id") var customer_id: String,
    @Json(name ="customer_email") var customer_email: String,
    @Json(name ="customer_mobile") var customer_mobile: String,
    @Json(name ="customer_name") var customer_name: String,
    @Json(name ="totalAmount") var totalAmount: String,
    @Json(name ="noOfTickets") var noOfTickets: String,
    @Json(name ="orderId") var orderId: String? = null,
    @Json(name ="checksum") var checksum: String? = null,


)
