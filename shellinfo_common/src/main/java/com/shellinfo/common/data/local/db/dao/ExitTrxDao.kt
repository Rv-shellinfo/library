package com.shellinfo.common.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shellinfo.common.data.local.db.entity.ExitTrxTable

@Dao
interface ExitTrxDao {

    @Query("DELETE FROM EXIT_TRANSACTION_TABLE WHERE IS_SYNC=1")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: ExitTrxTable)

    @Query("SELECT * FROM EXIT_TRANSACTION_TABLE WHERE IS_SYNC = 0")
    suspend fun getUnSyncedRecords(): List<ExitTrxTable>

    @Query("UPDATE EXIT_TRANSACTION_TABLE SET IS_SYNC = 1 WHERE TRANSACTION_ID = :id")
    suspend fun setDataToSync(id: String)
}