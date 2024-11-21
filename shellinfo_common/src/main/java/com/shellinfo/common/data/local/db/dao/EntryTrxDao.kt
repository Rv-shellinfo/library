package com.shellinfo.common.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shellinfo.common.data.local.db.entity.EntryTrxTable
import com.shellinfo.common.data.local.db.entity.PurchasePassTable

@Dao
interface EntryTrxDao {

    @Query("DELETE FROM ENTRY_TRANSACTION_TABLE WHERE IS_SYNC=1")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: EntryTrxTable)

    @Query("SELECT * FROM ENTRY_TRANSACTION_TABLE WHERE IS_SYNC = 0")
    suspend fun getUnSyncedRecords(): List<EntryTrxTable>

    @Query("UPDATE ENTRY_TRANSACTION_TABLE SET IS_SYNC = 1 WHERE TRANSACTION_ID = :id")
    suspend fun setDataToSync(id: String)
}