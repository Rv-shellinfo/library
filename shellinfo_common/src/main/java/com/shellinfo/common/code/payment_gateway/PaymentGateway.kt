package com.shellinfo.common.code.payment_gateway

import android.app.Activity
import android.content.Context
import com.shellinfo.common.data.remote.response.model.payment_gateway.AppPaymentRequest

/**
 * Common exposed payment gateway methods
 * Will be used in the individual applications
 */
interface PaymentGateway {

    fun processPayment(payRequest: AppPaymentRequest, context: Activity)
}