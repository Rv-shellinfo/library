package com.shellinfo.common.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shellinfo.common.utils.DBConstants

@Entity(tableName = DBConstants.DAILY_LIMITS_TABLE)
data class DailyLimitTable(

    @PrimaryKey
    @ColumnInfo(name = "DAILY_LIMIT_ID")
    val dailyLimitId: Int,

    @ColumnInfo(name = "OPERATOR_NAME_ID")
    val operatorNameId: Int,

    @ColumnInfo(name = "DAILY_LIMIT_VALUE")
    val dailyLimitValue: Int,

    @ColumnInfo(name = "VERSION")
    val version: Double,

    @ColumnInfo(name = "IS_ACTIVE")
    val isActive: Boolean
)
