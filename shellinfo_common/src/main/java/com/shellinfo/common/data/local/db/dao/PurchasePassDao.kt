package com.shellinfo.common.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shellinfo.common.data.local.db.entity.PurchasePassTable
import com.shellinfo.common.data.local.db.entity.StationsTable

@Dao
interface PurchasePassDao {

    @Query("DELETE FROM PURCHASE_PASS_TABLE WHERE IS_SYNC=1")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(purchasePassData: PurchasePassTable)

    @Query("SELECT * FROM PURCHASE_PASS_TABLE WHERE IS_SYNC = 0")
    suspend fun getUnSyncedRecords(): List<PurchasePassTable>

    @Query("UPDATE PURCHASE_PASS_TABLE SET IS_SYNC = 1 WHERE PASS_ID = :id")
    suspend fun setDataToSync(id: String)
}