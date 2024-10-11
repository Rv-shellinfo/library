package com.shellinfo.common.code

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shellinfo.common.data.local.db.entity.PassTable
import com.shellinfo.common.data.local.db.entity.StationsTable
import com.shellinfo.common.data.local.db.repository.DbRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class DatabaseCall @Inject constructor(
    private val dbRepository: DbRepository
):ViewModel(){


    private val _stationsLiveData = MutableLiveData<List<StationsTable>>()
    val stationsLiveData: LiveData<List<StationsTable>> get() = _stationsLiveData

    private val _stationLiveData = MutableLiveData<StationsTable>()
    val singleStationLiveData: LiveData<StationsTable> get() = _stationLiveData


    fun getAllStations(){
        viewModelScope.launch {
            val stations = dbRepository.getAllStations()
            _stationsLiveData.value=stations
        }
    }

    fun getStationsByCorridorId(id:Int){
        viewModelScope.launch {
            val stations = dbRepository.getAllStationsByCorridorId(id)
            _stationsLiveData.value=stations
        }
    }


    fun getStationsByCorridorName(name:String){
        viewModelScope.launch {
            val stations = dbRepository.getAllStationsByCorridorName(name)
            _stationsLiveData.value=stations
        }
    }

    fun searchStation(keyword:String){
        viewModelScope.launch {
            val stations = dbRepository.searchStation(keyword)
            _stationsLiveData.value=stations
        }
    }

    fun getStationByStationId(id:String){
        viewModelScope.launch {
            val station = dbRepository.getStationById(id)
            _stationLiveData.value=station
        }
    }

    fun addPassList(passList:List<PassTable>){
        viewModelScope.launch {
            dbRepository.insertPasses(passList)
        }
    }

    suspend fun getPassList():List<PassTable>{
        return withContext(Dispatchers.IO) {
            dbRepository.getAllPasses()
        }
    }


    suspend fun getPassInfo(passId:String):PassTable{
        return withContext(Dispatchers.IO) {
            dbRepository.getPassById(passId)
        }
    }

}