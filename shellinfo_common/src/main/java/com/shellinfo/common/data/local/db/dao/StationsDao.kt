package com.shellinfo.common.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shellinfo.common.data.local.db.entity.StationsTable
import com.shellinfo.common.utils.DBConstants

@Dao
interface StationsDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stations: List<StationsTable>)

    @Query("SELECT * FROM STATIONS_TABLE")
    suspend fun getAllStations(): List<StationsTable>

    @Query("SELECT * FROM STATIONS_TABLE WHERE CORRIDOR_ID = :corridorId")
    suspend fun getStationsByCorridorId(corridorId: Int): List<StationsTable>

    @Query("SELECT * FROM STATIONS_TABLE WHERE CORRIDOR_NAME = :corridorName")
    suspend fun getStationsByCorridorName(corridorName: String): List<StationsTable>

    @Query("SELECT * FROM STATIONS_TABLE WHERE STATION_NAME LIKE '%' || :keyword || '%' OR NAME LIKE '%' || :keyword || '%'")
    suspend fun searchStations(keyword: String): List<StationsTable>

    @Query("SELECT * FROM STATIONS_TABLE WHERE STATION_ID = :stationId LIMIT 1")
    suspend fun getStationById(stationId: String): StationsTable
}