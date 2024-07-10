package com.shellinfo.common.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shellinfo.common.data.local.db.entity.OrdersTable
import com.shellinfo.common.utils.DBConstants.ORDERS_TABLE

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ordersTable: OrdersTable)

    @Query("SELECT * FROM $ORDERS_TABLE")
    suspend fun getAllOrders(): List<OrdersTable>

    @Query("DELETE FROM $ORDERS_TABLE WHERE PURCHASE_ID = :purchaseId")
    suspend fun deleteOrderByPurchaseId(purchaseId:String)

    @Query("DELETE FROM $ORDERS_TABLE")
    suspend fun deleteAll()
}