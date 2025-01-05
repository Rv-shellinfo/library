 package com.shellinfo.common.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shellinfo.common.data.local.db.entity.OrdersTable
import com.shellinfo.common.data.local.db.entity.TicketBackupTable
import com.shellinfo.common.data.local.db.model.CountAndSumResult
import com.shellinfo.common.utils.DBConstants.TICKET_BACKUP_TABLE

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

     @Query(
         "SELECT COUNT(*) AS recordCount, IFNULL(SUM(PENALTY_AMOUNT), 0) AS totalPenalty " +
                 "FROM $TICKET_BACKUP_TABLE " +
                 "WHERE SHIFT_ID = :shiftId AND PAYMENT_MODE IN (:paymentModes) AND TRANSACTION_TYPE_ID = :transactionTypeId"
     )
     suspend fun getCountAndSumForCondition(
         shiftId: String,
         paymentModes: List<Int>,
         transactionTypeId: Int
     ): CountAndSumResult
}