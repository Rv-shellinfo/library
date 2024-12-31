package com.shellinfo.common.data.local.db.model

import androidx.room.ColumnInfo

data class CountAndSumResult(
    @ColumnInfo(name = "recordCount") val recordCount: Int,
    @ColumnInfo(name = "totalPenalty") val totalPenalty: Int
)
