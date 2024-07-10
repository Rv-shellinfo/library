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

    @ColumnInfo(name = "FROM_STATION_NAME")
    val fromStation: String?,

    @ColumnInfo(name = "TO_STATION_NAME")
    val toStationName: String?,

    @ColumnInfo(name = "UNIT_PRICE")
    val unitPrice: String?,

    @ColumnInfo(name = "TOTAL_FARE")
    val totalFare: String?,

    @ColumnInfo(name = "PURCHASE_ID")
    val purchaseId: String?,

    @ColumnInfo(name = "TICKET_ID")
    val ticketId: String?,

    @ColumnInfo(name = "J_TYPE")
    val jType: String?,

    @ColumnInfo(name = "PASSENGER_MONEY")
    val passengerMoney: String?,

    @ColumnInfo(name = "CHANGE_MONEY")
    val changeMoney: String?,

    @ColumnInfo(name = "NUMBER_OF_TICKET")
    val noOfTickets: String?,

    @ColumnInfo(name = "TRANS_DATE")
    val transDate: String?,

    @ColumnInfo(name = "TRANS_TIME")
    val transTime: String?,

    @ColumnInfo(name = "PAYMENT_MODE_TICKET")
    val paymentMode: String?

)
