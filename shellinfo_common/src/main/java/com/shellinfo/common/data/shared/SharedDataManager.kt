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
    private val _isLibraryInit = SingleLiveEvent<Event<Boolean>>()
    val isLibraryInit: LiveData<Event<Boolean>> get() = _isLibraryInit

    // Get all station data
    private val _stationData = SingleLiveEvent<Event<List<StationsTable>>>()
    val stationData: LiveData<Event<List<StationsTable>>> get() = _stationData

    // Get all station data
    private val _singleStationData = SingleLiveEvent<Event<StationsTable>>()
    val singleStationData: LiveData<Event<StationsTable>> get() = _singleStationData

    // Get trip limits data
    private val _tripLimitData = SingleLiveEvent<Event<List<TripLimitTable>>>()
    val tripLimitData: LiveData<Event<List<TripLimitTable>>> get() = _tripLimitData

    // Get daily limits data
    private val _dailyLimitData = SingleLiveEvent<Event<List<DailyLimitTable>>>()
    val dailyLimitData: LiveData<Event<List<DailyLimitTable>>> get() = _dailyLimitData

    // Get zone data
    private val _zoneData = SingleLiveEvent<Event<List<ZoneTable>>>()
    val zoneData: LiveData<Event<List<ZoneTable>>> get() = _zoneData

    //Get Pass data
    private val _passData = SingleLiveEvent<Event<List<PassTable>>>()
    val passData: LiveData<Event<List<PassTable>>> get() = _passData


    // Private mutable live data
    private val _csaData = SingleLiveEvent<Event<CSAMasterData?>>()

    // Public immutable live data
    val csaData: LiveData<Event<CSAMasterData?>> get() = _csaData


    // Private mutable live data
    private val _osaData = SingleLiveEvent<Event<OSAMasterData?>>()

    // Public immutable live data
    val osaData: LiveData<Event<OSAMasterData?>> get() = _osaData


    // Fare data
    private val _fareData = SingleLiveEvent<Event<ApiResponse<List<FareResponse?>>>>()
    val fareData: LiveData<Event<ApiResponse<List<FareResponse?>>>> get() = _fareData


    //Generate Ticket
    private val _ticketData = SingleLiveEvent<Event<ApiResponse<TicketResponse?>>>()
    val ticketData: LiveData<Event<ApiResponse<TicketResponse?>>> get() = _ticketData


    //device control commands
    private val _deviceControlCommand = SingleLiveEvent<Event<BaseMessageMqtt<*>>>()
    val deviceControlCommand: LiveData<Event<BaseMessageMqtt<*>>> get() = _deviceControlCommand


    //special mode commands
    private val _specialModeCommand = SingleLiveEvent<Event<BaseMessageMqtt<*>>>()
    val specialModeCommand: LiveData<Event<BaseMessageMqtt<*>>> get() = _specialModeCommand


    //SLE message
    private val _sleMessage = SingleLiveEvent<Event<BaseMessageMqtt<*>>>()
    val sleMessage: LiveData<Event<BaseMessageMqtt<*>>> get() = _sleMessage



    // Function to update csa data
    fun sendCsaData(value: CSAMasterData) {
        _csaData.postValue(Event(value))

    }

    // Function to update and send osa data
    fun sendOsaData(value: OSAMasterData) {
        _osaData.postValue(Event(value))
    }

    fun sendLibraryInit(value: Boolean) {
        _isLibraryInit.postValue(Event(value))
    }

    fun sendStationsData(value: List<StationsTable>) {
        _stationData.postValue(Event(value))
    }

    fun sendSingleStationData(value: StationsTable) {
        _singleStationData.postValue(Event(value))
    }

    fun sendPassData(value: List<PassTable>) {
        _passData.postValue(Event(value))
    }

    fun sendDailyLimitsData(value: List<DailyLimitTable>) {
        _dailyLimitData.postValue(Event(value))
    }

    fun sendTripLimitData(value: List<TripLimitTable>) {
        _tripLimitData.postValue(Event(value))
    }

    fun sendZoneData(value: List<ZoneTable>) {
        _zoneData.postValue(Event(value))
    }

    fun sendFareData(value:ApiResponse<List<FareResponse>>){
        _fareData.postValue(Event(value))
    }

    fun sendTicketData(value:ApiResponse<TicketResponse>){
        _ticketData.postValue(Event(value))
    }

    fun sendSpecialModes(value:BaseMessageMqtt<*>){
        _specialModeCommand.postValue(Event(value))
    }

    fun sendDeviceControlCommand(value:BaseMessageMqtt<*>){
        _deviceControlCommand.postValue(Event(value))
    }

    fun sendSleMessage(value:BaseMessageMqtt<*>){
        _sleMessage.postValue(Event(value))
    }
}