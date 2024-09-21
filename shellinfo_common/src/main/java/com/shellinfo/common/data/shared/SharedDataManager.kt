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
    private val _cardData = MutableLiveData<CSAMasterData>()

    // Public immutable live data
    val cardData: LiveData<CSAMasterData> get() = _cardData



    // Function to update card LiveData
    fun updateCardData(value: CSAMasterData) {
        Log.e("DATA",">>> DATA FOUND")
        _cardData.postValue(value)
        _cardData.value = value
    }
}