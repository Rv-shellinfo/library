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


    @ColumnInfo(name = "VERSION")
    val version: String,

    @ColumnInfo(name = "IS_DAILY_LIMIT_ENABLE")
    val isDailyLimitEnable: Boolean,

    @ColumnInfo(name = "IS_PASS_LIMIT_ENABLE")
    val isPassLimitEnable: Boolean,

    @ColumnInfo(name = "IS_ZONE_ENABLE")
    val isZoneEnable: Boolean,

    @ColumnInfo(name = "IS_TRIP_LIMIT_ENABLE")
    val isTripEnable: Boolean,

    @ColumnInfo(name = "IS_STATION_ENABLE")
    val isStationEnable: Boolean,

    @ColumnInfo(name = "IS_ACTIVE")
    val isActive: Boolean
)
