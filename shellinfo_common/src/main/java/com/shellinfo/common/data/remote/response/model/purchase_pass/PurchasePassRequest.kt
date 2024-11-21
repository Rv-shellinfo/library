package com.shellinfo.common.data.remote.response.model.purchase_pass

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PurchasePassRequest(
    @Json(name ="operatorId") val operatorId:Int,
    @Json(name ="passId") val passId:String,
    @Json(name ="merchantOrderId") val merchantOrderId:String,
    @Json(name ="merchantId") val merchantId:Int,
    @Json(name ="passTypeId") val passTypeId:Int,
    @Json(name ="passTypeCode") val passTypeCode:String,
    @Json(name ="totalAmount") val totalAmount:Double,
    @Json(name ="passStatusId") val passStatusId:Int,
    @Json(name ="passStatusCode") val passStatusCode:String,
    @Json(name ="purchaseDate") val purchaseDate:String,
    @Json(name ="expiryDate") val expiryDate:String,
    @Json(name ="stationId") val stationId:String,
    @Json(name ="equipmentId") val equipmentId:String,
    @Json(name ="fromStation") val fromStation:String,
    @Json(name ="toStation") val toStation:String,
    @Json(name ="zone") val zone:Int,
    @Json(name ="lines") val lines:Int,
    @Json(name ="tripLimit") val tripLimit:Int,
    @Json(name ="dailyLimit") val dailyLimit:Int,
    @Json(name ="paymentMethodId") val paymentMethodId:Int,
    @Json(name ="bankStan") val bankStan:String,
    @Json(name ="bankRrn") val bankRrn:String,
    @Json(name ="bankResponseCode") val bankResponseCode:String,
    @Json(name ="bankAid") val bankAid:String,
    @Json(name ="bankCardNumber") val bankCardNumber:String,
    @Json(name ="bankCardType") val bankCardType:String,
    @Json(name ="bankMid") val bankMid:String,
    @Json(name ="bankTid") val bankTid:String,
    @Json(name ="bankTransactionId") val bankTransactionId:String,
    @Json(name ="bankReferenceNumber") val bankReferenceNumber:String,
    @Json(name ="bankIssuerId") val bankIssuerId:String,
    @Json(name ="acquierBank") val acquierBank:String,
    @Json(name ="cardScheme") val cardScheme:String
)
