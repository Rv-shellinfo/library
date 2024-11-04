package com.shellinfo.common.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shellinfo.common.utils.DBConstants

@Entity(tableName = DBConstants.STATIONS_TABLE)
data class StationsTable(

    @PrimaryKey()
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

    @ColumnInfo(name = "IS_JUNCTION")
    val isJunction: Boolean?,

    @ColumnInfo(name = "ROUTE_COLOR_CODE")
    val routeColorCode: String?,

    @ColumnInfo(name = "MST_ID")
    val mstId: String?,

    @ColumnInfo(name = "STATUS")
    val status: String?

)