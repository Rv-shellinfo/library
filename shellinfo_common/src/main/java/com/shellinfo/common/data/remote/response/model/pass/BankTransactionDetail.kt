package com.shellinfo.common.data.remote.response.model.pass

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BankTransactionDetail(

    val paymentMethodId:Int,
    val bankCardNumber:String?="",
    val bankCardType:String?="",
    val bankStan:String?="",
    val bankRrn:String?="",
    val bankResponseCode:String?="",
    val bankResponsePayMode:String?="",
    val bankAid:String?="",
    val bankMid:String?="",
    val bankTid:String?="",
    val bankTransactionId:String?="",
    val bankReferenceNumber:String?="",
    val cardScheme:String?="",
    val acquierBank:String?="",
    val bankIssuerId:String?="",
)
