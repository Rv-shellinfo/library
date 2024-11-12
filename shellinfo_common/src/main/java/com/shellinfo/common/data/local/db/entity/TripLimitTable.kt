package com.shellinfo.common.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shellinfo.common.utils.DBConstants

@Entity(tableName = DBConstants.TRIP_LIMITS_TABLE)
data class TripLimitTable(

    @PrimaryKey
    @ColumnInfo(name = "TRIP_LIMIT_ID")
    val tripLimitId: Int,

    @ColumnInfo(name = "OPERATOR_NAME_ID")
    val operatorNameId: Int,

    @ColumnInfo(name = "TRIP_LIMIT_VALUE")
    val tripLimitValue: Int,

    @ColumnInfo(name = "VERSION")
    val version: Double,

    @ColumnInfo(name = "IS_ACTIVE")
    val isActive: Boolean
)
