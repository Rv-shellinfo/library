package com.shellinfo.common.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shellinfo.common.utils.DBConstants

@Entity(tableName = DBConstants.DAILY_LIMITS_TABLE)
data class DailyLimitTable(

    @PrimaryKey
    @ColumnInfo(name = "DAILY_LIMIT_ID")
    val tripLimitId: Int,

    @ColumnInfo(name = "OPERATOR_NAME_ID")
    val operatorNameId: Int,

    @ColumnInfo(name = "DAILY_LIMIT_VALUE")
    val tripLimitValue: String,

    @ColumnInfo(name = "ACTIVE_STATUS")
    val activeStatus: Boolean,

    @ColumnInfo(name = "PASS_DURATION_DAYS")
    val passDuration: Int,

    @ColumnInfo(name = "DAILY_LIMIT_DEFAULT")
    val dailyLimit: Int,

    @ColumnInfo(name = "PASS_LIMIT_DEFAULT")
    val passLimit: Int,

    @ColumnInfo(name = "VERSION")
    val version: Int,

    @ColumnInfo(name = "IS_ACTIVE")
    val isActive: Boolean
)
