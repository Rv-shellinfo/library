package com.shellinfo.common.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shellinfo.common.utils.DBConstants

@Entity(tableName = DBConstants.ORDERS_TABLE)
data class OrdersTable(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "UID")
    val uId: Int?=1,

    @ColumnInfo(name = "PURCHASE_ID")
    val purchaseId: String,

    @ColumnInfo(name = "FROM_STATION_NAME")
    val fromStation: String,

    @ColumnInfo(name = "TO_STATION_NAME")
    val toStation: String,

    @ColumnInfo(name = "UNIT_PRICE")
    val unitPrice: String,

    @ColumnInfo(name = "TRANS_DATE")
    val transDate: String
)