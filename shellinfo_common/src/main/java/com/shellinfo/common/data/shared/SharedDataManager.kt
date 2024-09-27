package com.shellinfo.common.data.shared

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shellinfo.common.data.local.data.emv_rupay.CSAMasterData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedDataManager @Inject constructor(){

    // Private mutable live data
    private val _responseData = MutableLiveData<CSAMasterData>()

    // Public immutable live data
    val responseData: LiveData<CSAMasterData> get() = _responseData



    // Function to update card LiveData
    fun updateResponseData(value: CSAMasterData) {
        Log.e("DATA",">>> DATA FOUND")
        _responseData.postValue(value)
    }
}