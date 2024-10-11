package com.shellinfo.common.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shellinfo.common.data.local.db.entity.PassTable
import com.shellinfo.common.data.local.db.entity.StationsTable

@Dao
interface PassDao {

    @Query("DELETE FROM PASS_TABLE")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(passes: List<PassTable>)

    @Query("SELECT * FROM PASS_TABLE")
    suspend fun getAllPasses(): List<PassTable>

    @Query("SELECT * FROM PASS_TABLE WHERE PASS_CODE = :passId LIMIT 1")
    suspend fun getPassById(passId: String): PassTable
}