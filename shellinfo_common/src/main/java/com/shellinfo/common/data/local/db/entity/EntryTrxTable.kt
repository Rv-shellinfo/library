package com.shellinfo.common.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shellinfo.common.utils.DBConstants

@Entity(tableName = DBConstants.ENTRY_TRANSACTION_TABLE)
data class EntryTrxTable(

    @PrimaryKey
    @ColumnInfo(name = "TRANSACTION_ID")
    val transactionId: String,

    @ColumnInfo(name = "TRANSACTION_SEQ_NUMBER")
    val trxSeqNumber: Long,

    @ColumnInfo(name = "TRANSACTION_TYPE")
    val transactionType: Int,

    @ColumnInfo(name = "LINE_ID")
    val lineId: String,

    @ColumnInfo(name = "STATION_ID")
    val stationId: String,

    @ColumnInfo(name = "EQUIPMENT_GROUP_ID")
    val equipmentGroupId: String,

    @ColumnInfo(name = "EQUIPMENT_ID")
    val equipmentId: String,

    @ColumnInfo(name = "ACQUIRER_ID")
    val aquirerId: String,

    @ColumnInfo(name = "OPERATOR_ID")
    val operatorId: String,

    @ColumnInfo(name = "TERMINAL_ID")
    val terminalId: String,

    @ColumnInfo(name = "CARD_TYPE")
    val cardType: Int,

    @ColumnInfo(name = "PAN_SHA")
    val panSha: String,

    @ColumnInfo(name = "PRODUCT_TYPE")
    val productType: Int,

    @ColumnInfo(name = "CARD_BIN")
    val cardBin: String,

    @ColumnInfo(name = "PEAK_NON_PEAK_TYPE_ID")
    val peakNonPeakTypeId: Int,

    @ColumnInfo(name = "BUSINESS_DATE")
    val businessDate: String,

    @ColumnInfo(name = "TRANSACTION_DATE_TIME")
    val transactionDateTime: String,

    @ColumnInfo(name = "PASS_START_DATE")
    val passStartDate: String,

    @ColumnInfo(name = "PASS_END_DATE")
    val passEndDate: String,

    @ColumnInfo(name = "PASS_STATION_ONE")
    val passStationOne: String,

    @ColumnInfo(name = "PASS_STATION_TWO")
    val passStationTwo: String,

    @ColumnInfo(name = "PASS_BALANCE")
    val passBalance: String,

    @ColumnInfo(name = "IS_SYNC")
    val isSync: Boolean =false
)
