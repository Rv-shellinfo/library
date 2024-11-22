package com.shellinfo.common.code

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shellinfo.common.data.local.db.entity.PassTable
import com.shellinfo.common.data.local.db.entity.StationsTable
import com.shellinfo.common.data.local.db.entity.ZoneTable
import com.shellinfo.common.data.local.db.repository.DbRepository
import com.shellinfo.common.data.shared.SharedDataManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class DatabaseCall @Inject constructor(
    private val dbRepository: DbRepository,
    private val sharedDataManager: SharedDataManager
):ViewModel(){


    private val _stationsLiveData = MutableLiveData<List<StationsTable>>()
    val stationsLiveData: LiveData<List<StationsTable>> get() = _stationsLiveData

    private val _stationLiveData = MutableLiveData<StationsTable>()
    val singleStationLiveData: LiveData<StationsTable> get() = _stationLiveData


    fun getAllStations(){
        viewModelScope.launch {
            val stations = dbRepository.getAllStations()
            sharedDataManager.sendStationsData(stations)
        }
    }

    fun getStationsByCorridorId(id:Int){
        viewModelScope.launch {
            val stations = dbRepository.getAllStationsByCorridorId(id)
            sharedDataManager.sendStationsData(stations)
        }
    }


    fun getStationsByCorridorName(name:String){
        viewModelScope.launch {
            val stations = dbRepository.getAllStationsByCorridorName(name)
            sharedDataManager.sendStationsData(stations)
        }
    }

    fun searchStation(keyword:String){
        viewModelScope.launch {
            val stations = dbRepository.searchStation(keyword)
            sharedDataManager.sendStationsData(stations)
        }
    }

   suspend fun getStationByStationId(id:String):StationsTable{
       return dbRepository.getStationById(id)
    }

    suspend fun getStationByStationId(id:Int):StationsTable{
        return dbRepository.getStationById(id)
    }

    fun getStationByStationIdNew(id:String){
        viewModelScope.launch {
            val station = dbRepository.getStationById(id)
            sharedDataManager.sendSingleStationData(station)
        }
    }


    fun getPassData(){
        viewModelScope.launch {
            val passData = dbRepository.getAllPasses()
            sharedDataManager.sendPassData(passData)
        }
    }

    suspend fun getPassById(id:Int):PassTable{
        return dbRepository.getPassById(id)
    }

    fun getDailyLimits(){
        viewModelScope.launch {
            val dailyLimitData = dbRepository.getAllDailyLimits()
            sharedDataManager.sendDailyLimitsData(dailyLimitData)
        }
    }

    fun getTripLimits(){
        viewModelScope.launch {
            val tripLimitData = dbRepository.getAllTripLimits()
            sharedDataManager.sendTripLimitData(tripLimitData)
        }
    }

    fun getZoneData(){
        viewModelScope.launch {
            val zoneData = dbRepository.getAllZones()
            sharedDataManager.sendZoneData(zoneData)
        }
    }

    suspend fun getZoneById(id:Int):ZoneTable{
        return dbRepository.getZoneById(id)
    }

}