package com.shellinfo.common.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shellinfo.common.utils.DBConstants

@Entity(tableName = DBConstants.STATIONS_TABLE)
data class StationsTable(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    val id: Int = 0, // Auto-incremented serial number

    @ColumnInfo(name = "STATION_ID")
    val stationId: String,

    @ColumnInfo(name = "OPERATOR_NAME_ID")
    val operatorNameId: Int,

    @ColumnInfo(name = "STATION_NAME")
    val stationName: String?,

    @ColumnInfo(name = "NAME")
    val name: String?,

    @ColumnInfo(name = "SHORT_NAME")
    val shortName: String?,

    @ColumnInfo(name = "CORRIDOR_ID")
    val corridorId: Int?,

    @ColumnInfo(name = "CORRIDOR_NAME")
    val corridorName: String?,

    @ColumnInfo(name = "LATITUDE")
    val latitude: Double?,

    @ColumnInfo(name = "LONGITUDE")
    val longitude: Double?,

    @ColumnInfo(name = "POS_X")
    val posX: Double?,

    @ColumnInfo(name = "POS_Y")
    val posY: Double?,

    @ColumnInfo(name = "IS_JUNCTION")
    val isJunction: Boolean?,

    @ColumnInfo(name = "ROUTE_COLOR_CODE")
    val routeColorCode: String?,

    @ColumnInfo(name = "VALID_FROM_DATE")
    val validFromDate: String?,

    @ColumnInfo(name = "VALID_TO_DATE")
    val validToDate: String?,

    @ColumnInfo(name = "CC_IP")
    val ccIp: String?,

    @ColumnInfo(name = "SC_IP")
    val scIp: String?,

    @ColumnInfo(name = "STATUS")
    val status: Boolean?

)