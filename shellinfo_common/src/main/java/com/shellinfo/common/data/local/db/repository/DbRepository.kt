package com.shellinfo.common.data.local.db.repository

import com.shellinfo.common.data.local.db.dao.DailyLimitDao
import com.shellinfo.common.data.local.db.dao.OrderDao
import com.shellinfo.common.data.local.db.dao.PassDao
import com.shellinfo.common.data.local.db.dao.StationsDao
import com.shellinfo.common.data.local.db.dao.TicketBackupDao
import com.shellinfo.common.data.local.db.dao.TripLimitDao
import com.shellinfo.common.data.local.db.dao.ZoneDao
import com.shellinfo.common.data.local.db.entity.DailyLimitTable
import com.shellinfo.common.data.local.db.entity.OrdersTable
import com.shellinfo.common.data.local.db.entity.PassTable
import com.shellinfo.common.data.local.db.entity.StationsTable
import com.shellinfo.common.data.local.db.entity.TicketBackupTable
import com.shellinfo.common.data.local.db.entity.TripLimitTable
import com.shellinfo.common.data.local.db.entity.ZoneTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DbRepository @Inject constructor(
    private val stationsDao: StationsDao,
    private val orderDao: OrderDao,
    private val ticketBackupDao: TicketBackupDao,
    private val passDao: PassDao,
    private val dailyLimitDao: DailyLimitDao,
    private val tripLimitDao: TripLimitDao,
    private val zoneDao: ZoneDao){

    suspend fun insertStations(stations:List<StationsTable>){
         stationsDao.deleteAll()
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

    suspend fun insertPasses(passList:List<PassTable>){
        passDao.deleteAll()
        passDao.insert(passList)
    }

    suspend fun getPassById(passId:String):PassTable{
        return passDao.getPassById(passId)
    }

    suspend fun getAllPasses():List<PassTable>{
        return passDao.getAllPasses()
    }

    suspend fun insertDailyLimit(dailLimitList:List<DailyLimitTable>){
        dailyLimitDao.deleteAll()
        dailyLimitDao.insert(dailLimitList)
    }

    suspend fun getDailyLimit(id:Int):DailyLimitTable{
        return dailyLimitDao.getDailyLimitById(id)
    }

    suspend fun getAllDailyLimits():List<DailyLimitTable>{
        return dailyLimitDao.getAllDailyLimits()
    }

    suspend fun insertTripLimits(tripLimitList:List<TripLimitTable>){
        tripLimitDao.deleteAll()
        tripLimitDao.insert(tripLimitList)
    }

    suspend fun getTripLimit(id:Int):TripLimitTable{
        return tripLimitDao.getTripLimitById(id)
    }

    suspend fun getAllTripLimits():List<TripLimitTable>{
        return tripLimitDao.getAllTripLimits()
    }

    suspend fun insertZones(zoneList:List<ZoneTable>){
        zoneDao.deleteAll()
        zoneDao.insert(zoneList)
    }

    suspend fun getZoneById(id:Int):ZoneTable{
        return withContext(Dispatchers.IO){
            zoneDao.getZoneById(id)
        }
    }

    suspend fun getAllZones():List<ZoneTable>{
        return zoneDao.getAllZones()
    }
}