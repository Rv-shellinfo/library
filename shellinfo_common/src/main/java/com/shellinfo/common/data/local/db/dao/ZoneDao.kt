package com.shellinfo.common.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shellinfo.common.data.local.db.entity.ZoneTable

@Dao
interface ZoneDao {

    @Query("DELETE FROM ZONE_TABLE")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tripLimitTable: List<ZoneTable>)

    @Query("SELECT * FROM ZONE_TABLE")
    suspend fun getAllZones(): List<ZoneTable>

    @Query("SELECT * FROM ZONE_TABLE WHERE ZONE_ID = :zoneId LIMIT 1")
    suspend fun getZoneById(zoneId: Int): ZoneTable
}