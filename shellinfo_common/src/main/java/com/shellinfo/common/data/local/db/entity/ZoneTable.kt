package com.shellinfo.common.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shellinfo.common.utils.DBConstants

@Entity(tableName = DBConstants.ZONE_TABLE)
data class ZoneTable(

    @PrimaryKey
    @ColumnInfo(name = "ZONE_ID")
    val zoneId: Int,

    @ColumnInfo(name = "OPERATOR_NAME_ID")
    val operatorNameId: Int,

    @ColumnInfo(name = "ZONE_NAME")
    val zoneName: String,

    @ColumnInfo(name = "ZONE_AMOUNT")
    val zoneAmount: Double,

    @ColumnInfo(name = "ACTIVE_STATUS")
    val activeStatus: Boolean,


    @ColumnInfo(name = "VERSION")
    val version: Double
)
