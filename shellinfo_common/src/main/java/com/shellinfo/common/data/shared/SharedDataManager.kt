package com.shellinfo.common.data.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shellinfo.common.data.local.data.emv_rupay.CSAMasterData
import com.shellinfo.common.data.local.data.emv_rupay.OSAMasterData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedDataManager @Inject constructor(){

    // Private mutable live data
    private val _csaData = MutableLiveData<CSAMasterData>()

    // Public immutable live data
    val csaData: LiveData<CSAMasterData> get() = _csaData


    // Private mutable live data
    private val _osaData = MutableLiveData<OSAMasterData>()

    // Public immutable live data
    val osaData: LiveData<OSAMasterData> get() = _osaData


    // Function to update card LiveData
    fun sendCsaData(value: CSAMasterData) {
        _csaData.postValue(value)
    }


    fun sendOsaData(value:OSAMasterData){
        _osaData.postValue(value)
    }
}