package com.shellinfo.common.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shellinfo.common.data.local.db.entity.DailyLimitTable
import com.shellinfo.common.data.local.db.entity.TripLimitTable

@Dao
interface DailyLimitDao {

    @Query("DELETE FROM DAILY_LIMITS_TABLE")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dailLimitList: List<DailyLimitTable>)

    @Query("SELECT * FROM DAILY_LIMITS_TABLE")
    suspend fun getAllDailyLimits(): List<DailyLimitTable>

    @Query("SELECT * FROM DAILY_LIMITS_TABLE WHERE DAILY_LIMIT_ID = :dailyLimitId LIMIT 1")
    suspend fun getDailyLimitById(dailyLimitId: Int): DailyLimitTable
}