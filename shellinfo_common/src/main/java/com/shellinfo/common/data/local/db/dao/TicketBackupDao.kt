package com.shellinfo.common.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shellinfo.common.data.local.db.entity.OrdersTable
import com.shellinfo.common.data.local.db.entity.TicketBackupTable

@Dao
interface TicketBackupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ticketBackupTable: TicketBackupTable)

    @Query("SELECT * FROM TICKET_BACKUP_TABLE")
    suspend fun getAllTicketBackupData(): List<TicketBackupTable>

    @Query("DELETE FROM TICKET_BACKUP_TABLE WHERE UID = :uid")
    suspend fun delete(uid:String)

    @Query("DELETE FROM TICKET_BACKUP_TABLE")
    suspend fun deleteAll()
}