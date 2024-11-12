package com.shellinfo.common.data.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shellinfo.common.data.local.data.emv_rupay.CSAMasterData
import com.shellinfo.common.data.local.data.emv_rupay.OSAMasterData
import com.shellinfo.common.data.local.db.entity.DailyLimitTable
import com.shellinfo.common.data.local.db.entity.PassTable
import com.shellinfo.common.data.local.db.entity.StationsTable
import com.shellinfo.common.data.local.db.entity.TripLimitTable
import com.shellinfo.common.data.local.db.entity.ZoneTable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedDataManager @Inject constructor(){


    // Library properly init indicator
    private val _isLibraryInit = MutableLiveData<Boolean>()
    val isLibraryInit: LiveData<Boolean> get() = _isLibraryInit

    // Get all station data
    private val _stationData = MutableLiveData<List<StationsTable>>()
    val stationData:LiveData<List<StationsTable>> get() = _stationData

    // Get all station data
    private val _singleStationData = MutableLiveData<StationsTable>()
    val singleStationData:LiveData<StationsTable> get() = _singleStationData

    // Get trip limits data
    private val _tripLimitData = MutableLiveData<List<TripLimitTable>>()
    val tripLimitData:LiveData<List<TripLimitTable>> get() = _tripLimitData

    // Get daily limits data
    private val _dailyLimitData = MutableLiveData<List<DailyLimitTable>>()
    val dailyLimitData:LiveData<List<DailyLimitTable>> get() = _dailyLimitData

    // Get zone data
    private val _zoneData = MutableLiveData<List<ZoneTable>>()
    val zoneData:LiveData<List<ZoneTable>> get() = _zoneData

    //Get Pass data
    private val _passData = MutableLiveData<List<PassTable>>()
    val passData:LiveData<List<PassTable>> get() = _passData





    // Private mutable live data
    private val _csaData = MutableLiveData<CSAMasterData>()

    // Public immutable live data
    val csaData: LiveData<CSAMasterData> get() = _csaData


    // Private mutable live data
    private val _osaData = MutableLiveData<OSAMasterData>()

    // Public immutable live data
    val osaData: LiveData<OSAMasterData> get() = _osaData





    // Function to update csa data
    fun sendCsaData(value: CSAMasterData) {
        _csaData.postValue(value)
    }

    // Function to update and send osa data
    fun sendOsaData(value:OSAMasterData){
        _osaData.postValue(value)
    }

    fun sendLibraryInit(value:Boolean){
        _isLibraryInit.postValue(value)
    }

    fun sendStationsData(value:List<StationsTable>){
        _stationData.postValue(value)
    }

    fun sendSingleStationData(value:StationsTable){
        _singleStationData.postValue(value)
    }

    fun sendPassData(value:List<PassTable>){
        _passData.postValue(value)
    }

    fun sendDailyLimitsData(value:List<DailyLimitTable>){
        _dailyLimitData.postValue(value)
    }

    fun sendTripLimitData(value:List<TripLimitTable>){
        _tripLimitData.postValue(value)
    }

    fun sendZoneData(value:List<ZoneTable>){
        _zoneData.postValue(value)
    }
}