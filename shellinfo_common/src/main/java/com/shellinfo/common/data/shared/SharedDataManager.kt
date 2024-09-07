package com.shellinfo.common.data.shared

import androidx.lifecycle.MutableLiveData
import com.shellinfo.common.data.local.data.emv_rupay.CSAMasterData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedDataManager @Inject constructor(){

    val cardData = MutableLiveData<CSAMasterData>()


    // Function to update card LiveData
    fun updateCardData(value: CSAMasterData) {
        cardData.postValue(value)
    }
}