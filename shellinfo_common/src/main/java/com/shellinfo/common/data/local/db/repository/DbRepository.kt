package com.shellinfo.common.data.local.db.repository

import com.shellinfo.common.data.local.db.dao.OrderDao
import com.shellinfo.common.data.local.db.dao.StationsDao
import com.shellinfo.common.data.local.db.dao.TicketBackupDao
import com.shellinfo.common.data.local.db.entity.OrdersTable
import com.shellinfo.common.data.local.db.entity.StationsTable
import com.shellinfo.common.data.local.db.entity.TicketBackupTable
import javax.inject.Inject

class DbRepository @Inject constructor(
    private val stationsDao: StationsDao,
    private val orderDao: OrderDao,
    private val ticketBackupDao: TicketBackupDao){

    suspend fun insertStations(stations:List<StationsTable>){
         stationsDao.insert(stations)
    }

    suspend fun getAllStations():List<StationsTable>{
        return stationsDao.getAllStations()
    }

    suspend fun getAllStationsByCorridorId(id:Int):List<StationsTable>{
        return stationsDao.getStationsByCorridorId(id)
    }

    suspend fun getAllStationsByCorridorName(name:String):List<StationsTable>{
        return stationsDao.getStationsByCorridorName(name)
    }

    suspend fun searchStation(keyword:String):List<StationsTable>{
        return stationsDao.searchStations(keyword)
    }

    suspend fun getStationById(stationId:String):StationsTable{
        return stationsDao.getStationById(stationId)
    }

    suspend fun insertOrder(ordersTable: OrdersTable){
        orderDao.insert(ordersTable)
    }

    suspend fun deleteOrderByPurchaseId(id:String){
        orderDao.deleteOrderByPurchaseId(id)
    }

    suspend fun insertTicket(ticketBackupTable: TicketBackupTable){
        ticketBackupDao.insert(ticketBackupTable)
    }
}