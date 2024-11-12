package com.shellinfo.common.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shellinfo.common.utils.DBConstants

@Entity(tableName = DBConstants.PASS_TABLE)
data class PassTable(

    @PrimaryKey
    @ColumnInfo(name = "PASS_ID")
    val passId: Int,

    @ColumnInfo(name = "PASS_CODE")
    val passCode: String,

    @ColumnInfo(name = "OPERATOR_NAME_ID")
    val operatorNameId: Int,

    @ColumnInfo(name = "PASS_NAME")
    val passName: String,

    @ColumnInfo(name = "PASS_PRIORITY")
    val passPriority: Int,

    @ColumnInfo(name = "PASS_DURATION_DAYS")
    val passDuration: Int,

    @ColumnInfo(name = "DAILY_LIMIT_DEFAULT")
    val dailyLimitDefault: Int,

    @ColumnInfo(name = "PASS_LIMIT_DEFAULT")
    val passLimitDefault: Int,

    @ColumnInfo(name = "IS_DAILY_LIMIT_ACTIVE")
    val isDailyLimitActive: Boolean,

    @ColumnInfo(name = "IS_PASS_LIMIT_ACTIVE")
    val isPassLimitActive: Boolean,

    @ColumnInfo(name = "IS_ZONE_ACTIVE")
    val isZoneActive: Boolean,

    @ColumnInfo(name = "VERSION")
    val version: String,

    @ColumnInfo(name = "IS_ACTIVE")
    val isActive: Boolean
)
