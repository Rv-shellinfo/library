package com.shellinfo.common.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shellinfo.common.data.local.db.entity.TripLimitTable

@Dao
interface TripLimitDao {

    @Query("DELETE FROM TRIP_LIMITS_TABLE")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tripLimitTable: List<TripLimitTable>)

    @Query("SELECT * FROM TRIP_LIMITS_TABLE")
    suspend fun getAllTripLimits(): List<TripLimitTable>

    @Query("SELECT * FROM TRIP_LIMITS_TABLE WHERE TRIP_LIMIT_ID = :tripLimitId LIMIT 1")
    suspend fun getTripLimitById(tripLimitId: Int): TripLimitTable
}