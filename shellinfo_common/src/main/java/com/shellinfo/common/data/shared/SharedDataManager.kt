package com.shellinfo.common.data.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shellinfo.common.data.local.data.emv_rupay.CSAMasterData
import com.shellinfo.common.data.local.data.emv_rupay.OSAMasterData
import com.shellinfo.common.data.local.data.mqtt.BaseMessageMqtt
import com.shellinfo.common.data.local.data.mqtt.DeviceControlMessage
import com.shellinfo.common.data.local.data.mqtt.MqttData
import com.shellinfo.common.data.local.data.mqtt.SpecialModeMessage
import com.shellinfo.common.data.local.db.entity.DailyLimitTable
import com.shellinfo.common.data.local.db.entity.PassTable
import com.shellinfo.common.data.local.db.entity.StationsTable
import com.shellinfo.common.data.local.db.entity.TripLimitTable
import com.shellinfo.common.data.local.db.entity.ZoneTable
import com.shellinfo.common.data.remote.response.ApiResponse
import com.shellinfo.common.data.remote.response.model.fare.FareResponse
import com.shellinfo.common.data.remote.response.model.ticket.TicketResponse
import com.shellinfo.common.utils.Event
import com.shellinfo.common.utils.SingleLiveEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedDataManager @Inject constructor() {


    // Library properly init indicator
    private val _isLibraryInit = SingleLiveEvent<Boolean>()
    val isLibraryInit: LiveData<Boolean> get() = _isLibraryInit

    // Get all station data
    private val _stationData = SingleLiveEvent<List<StationsTable>>()
    val stationData: LiveData<List<StationsTable>> get() = _stationData

    // Get all station data
    private val _singleStationData = SingleLiveEvent<StationsTable>()
    val singleStationData: LiveData<StationsTable> get() = _singleStationData

    // Get trip limits data
    private val _tripLimitData = SingleLiveEvent<List<TripLimitTable>>()
    val tripLimitData: LiveData<List<TripLimitTable>> get() = _tripLimitData

    // Get daily limits data
    private val _dailyLimitData = SingleLiveEvent<List<DailyLimitTable>>()
    val dailyLimitData: LiveData<List<DailyLimitTable>> get() = _dailyLimitData

    // Get zone data
    private val _zoneData = SingleLiveEvent<List<ZoneTable>>()
    val zoneData: LiveData<List<ZoneTable>> get() = _zoneData

    //Get Pass data
    private val _passData = SingleLiveEvent<List<PassTable>>()
    val passData: LiveData<List<PassTable>> get() = _passData


    // Private mutable live data
    private val _csaData = SingleLiveEvent<CSAMasterData?>()

    // Public immutable live data
    val csaData: LiveData<CSAMasterData?> get() = _csaData


    // Private mutable live data
    private val _osaData = SingleLiveEvent<OSAMasterData?>()

    // Public immutable live data
    val osaData: LiveData<OSAMasterData?> get() = _osaData


    // Fare data
    private val _fareData = SingleLiveEvent<ApiResponse<List<FareResponse?>>>()
    val fareData: LiveData<ApiResponse<List<FareResponse?>>> get() = _fareData


    //Generate Ticket
    private val _ticketData = SingleLiveEvent<ApiResponse<TicketResponse?>>()
    val ticketData: LiveData<ApiResponse<TicketResponse?>> get() = _ticketData


    //device control commands
    private val _deviceControlCommand = SingleLiveEvent<BaseMessageMqtt<*>>()
    val deviceControlCommand: LiveData<BaseMessageMqtt<*>> get() = _deviceControlCommand


    //special mode commands
    private val _specialModeCommand = SingleLiveEvent<BaseMessageMqtt<*>>()
    val specialModeCommand: LiveData<BaseMessageMqtt<*>> get() = _specialModeCommand


    //SLE message
    private val _sleMessage = SingleLiveEvent<BaseMessageMqtt<*>>()
    val sleMessage: LiveData<BaseMessageMqtt<*>>get() = _sleMessage



    // Function to update csa data
    fun sendCsaData(value: CSAMasterData) {
        _csaData.postValue(value)

    }

    // Function to update and send osa data
    fun sendOsaData(value: OSAMasterData) {
        _osaData.postValue(value)
    }

    fun sendLibraryInit(value: Boolean) {
        _isLibraryInit.postValue(value)
    }

    fun sendStationsData(value: List<StationsTable>) {
        _stationData.postValue(value)
    }

    fun sendSingleStationData(value: StationsTable) {
        _singleStationData.postValue(value)
    }

    fun sendPassData(value: List<PassTable>) {
        _passData.postValue(value)
    }

    fun sendDailyLimitsData(value: List<DailyLimitTable>) {
        _dailyLimitData.postValue(value)
    }

    fun sendTripLimitData(value: List<TripLimitTable>) {
        _tripLimitData.postValue(value)
    }

    fun sendZoneData(value: List<ZoneTable>) {
        _zoneData.postValue(value)
    }

    fun sendFareData(value:ApiResponse<List<FareResponse>>){
        _fareData.postValue(value)
    }

    fun sendTicketData(value:ApiResponse<TicketResponse>){
        _ticketData.postValue(value)
    }

    fun sendSpecialModes(value:BaseMessageMqtt<*>){
        _specialModeCommand.postValue(value)
    }

    fun sendDeviceControlCommand(value:BaseMessageMqtt<*>){
        _deviceControlCommand.postValue(value)
    }

    fun sendSleMessage(value:BaseMessageMqtt<*>){
        _sleMessage.postValue(value)
    }
}