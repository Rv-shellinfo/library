package com.shellinfo.common.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shellinfo.common.utils.DBConstants

@Entity(tableName = DBConstants.PURCHASE_PASS_TABLE)
data class PurchasePassTable(

    @PrimaryKey
    @ColumnInfo(name = "PASS_ID")
    val passId: String,

    @ColumnInfo(name = "OPERATOR_ID")
    val operatorId: String,

    @ColumnInfo(name = "MERCHANT_ORDER_ID")
    val merchantOrderId: String,

    @ColumnInfo(name = "MERCHANT_ID")
    val merchantId: String,

    @ColumnInfo(name = "PASS_TYPE_ID")
    val passTypeId: Int,

    @ColumnInfo(name = "PASS_TYPE_CODE")
    val passTypeCode: String,

    @ColumnInfo(name = "TOTAL_AMOUNT")
    val totalAmount: Double,

    @ColumnInfo(name = "PASS_STATUS_ID")
    val passStatusId: Int,

    @ColumnInfo(name = "PASS_STATUS_CODE")
    val passStatusCode: String,

    @ColumnInfo(name = "PURCHASE_DATE_TIME")
    val purchaseDate: String,

    @ColumnInfo(name = "EXPIRY_DATE")
    val expiryDate: String,

    @ColumnInfo(name = "STATION_ID")
    val stationId: String,

    @ColumnInfo(name = "EQUIPMENT_ID")
    val equipmentId: String,

    @ColumnInfo(name = "FROM_STATION")
    val fromStation: String,

    @ColumnInfo(name = "TO_STATION")
    val toStation: String,

    @ColumnInfo(name = "ZONE_ID")
    val zone: Int,

    @ColumnInfo(name = "LINE_ID")
    val lines: Int,

    @ColumnInfo(name = "TRIP_LIMIT")
    val tripLimit: Int,

    @ColumnInfo(name = "DAILY_LIMIT")
    val dailyLimit: Int,

    @ColumnInfo(name = "PAYMENT_METHOD_ID")
    val paymentMethodId: Int,

    @ColumnInfo(name = "BANK_STAN")
    val bankStan: String,

    @ColumnInfo(name = "BANK_RRN")
    val bankRrn: String,

    @ColumnInfo(name = "BANK_RESPONSE_CODE")
    val bankResponseCode: String,

    @ColumnInfo(name = "BANK_AID")
    val bankAid: String,

    @ColumnInfo(name = "BANK_CARD_NUMBER")
    val bankCardNumber: String,

    @ColumnInfo(name = "BANK_CARD_TYPE")
    val bankCardType: String,

    @ColumnInfo(name = "BANK_MID")
    val bankMid: String,

    @ColumnInfo(name = "BANK_TID")
    val bankTid: String,

    @ColumnInfo(name = "BANK_TRANSACTION_ID")
    val bankTransactionId: String,

    @ColumnInfo(name = "BANK_REFERENCE_NUMBER")
    val bankReferenceNumber: String,

    @ColumnInfo(name = "BANK_ISSUER_ID")
    val bankIssuerId: String,

    @ColumnInfo(name = "ACQUIER_BANK")
    val acquierBank: String,

    @ColumnInfo(name = "CARD_SCHEME")
    val cardScheme: String,

    @ColumnInfo(name = "IS_SYNC")
    val isSync: Boolean? =false
)