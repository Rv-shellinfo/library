package com.shellinfo.common.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shellinfo.common.utils.DBConstants

@Entity(tableName = DBConstants.PASS_TABLE)
data class PassTable(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "PASS_ID")
    val uId: Int?=null,

    @ColumnInfo(name = "PASS_CODE")
    val passCode: String,

    @ColumnInfo(name = "PASS_NAME")
    val passName: String,

    @ColumnInfo(name = "PASS_PRIORITY")
    val passPriority: Int,

    @ColumnInfo(name = "PASS_DURATION")
    val passDuration: Int,

    @ColumnInfo(name = "DAILY_LIMIT")
    val dailyLimit: Int,

    @ColumnInfo(name = "PASS_AMOUNT")
    val passAmount: Int,

    @ColumnInfo(name = "VERSION")
    val version: Int,

    @ColumnInfo(name = "IS_ACTIVE")
    val isActive: Boolean,
)
