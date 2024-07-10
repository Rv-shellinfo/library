package com.shellinfo.common.code.payment_gateway

import android.app.Activity
import android.content.Context
import com.shellinfo.common.data.remote.response.model.payment_gateway.AppPaymentRequest

class PaymentProcessor(private val paymentGateway: PaymentGateway) {

    fun processPayment(payRequest: AppPaymentRequest, context: Activity) {
        paymentGateway.processPayment(payRequest,context)
    }
}