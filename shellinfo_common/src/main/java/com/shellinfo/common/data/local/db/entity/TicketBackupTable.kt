package com.shellinfo.common.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shellinfo.common.utils.DBConstants

@Entity(tableName = DBConstants.TICKET_BACKUP_TABLE)
data class TicketBackupTable(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "UID")
    val uId: Int? = 0,

    @ColumnInfo(name = "SHIFT_ID")
    val shiftId: String? ,

    @ColumnInfo(name = "OPERATOR_ID")
    val operatorId: String?,

    @ColumnInfo(name = "PENALTY_AMOUNT")
    val penaltyAmount: Double?,

    @ColumnInfo(name = "FROM_STATION_ID")
    val fromStationId: String?,

    @ColumnInfo(name = "TO_STATION_ID")
    val toStationId: String?,

    @ColumnInfo(name = "UNIT_PRICE")
    val unitPrice: Double?,

    @ColumnInfo(name = "TOTAL_FARE")
    val totalFare: Double?,

    @ColumnInfo(name = "PURCHASE_ID")
    val purchaseId: String?,

    @ColumnInfo(name = "TICKET_ID")
    val ticketId: String?,

    @ColumnInfo(name = "TICKET_TYPE")
    val ticketType: Int?,

    @ColumnInfo(name = "J_TYPE")
    val jType: String?,

    @ColumnInfo(name = "PASSENGER_MONEY")
    val passengerMoney: String?,

    @ColumnInfo(name = "CHANGE_MONEY")
    val changeMoney: String?,

    @ColumnInfo(name = "NUMBER_OF_TICKET")
    val noOfTickets: Int?,

    @ColumnInfo(name = "TRANSACTION_DATE")
    val transactionDate: String?,

    @ColumnInfo(name = "TRANSACTION_TYPE_ID")
    val transactionTypeId: Int?,

    @ColumnInfo(name = "TRANSACTION_TYPE")
    val transactionType: String?,

    @ColumnInfo(name = "PAYMENT_MODE")
    val paymentMode: Int,

    @ColumnInfo(name = "TID")
    val tid: String?,

    @ColumnInfo(name = "PAYMENT_CHANNEL")
    val paymentChannel: Int,

    @ColumnInfo(name = "BANK_TRANSACTION_ID")
    val bankTransactionId: String?,

    @ColumnInfo(name = "BANK_REFERENCE_NUMBER")
    val bankReferenceNumber: String?,

    @ColumnInfo(name = "VOUCHER_CODE")
    val voucherCode: String



)
