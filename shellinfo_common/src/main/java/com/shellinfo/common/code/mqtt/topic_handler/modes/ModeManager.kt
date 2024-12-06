package com.shellinfo.common.code.mqtt.topic_handler.modes

import android.content.Context
import androidx.lifecycle.LiveData
import com.shellinfo.common.code.enums.ModeType
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import com.shellinfo.common.utils.SingleLiveEvent
import com.shellinfo.common.utils.SpConstants.CURRENT_MODE
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModeManager @Inject constructor(
    private val spUtils:SharedPreferenceUtil
){
    private var currentMode: ModeType? = null

    //live data to listen the changes
    private val _currentModeLiveData = SingleLiveEvent<ModeType?>()
    val currentModeLiveData: LiveData<ModeType?> get() = _currentModeLiveData

    fun setMode(mode: ModeType) {
        currentMode = mode
        saveMode(mode)
        notifyModeChange(mode)
    }

    fun getCurrentMode(): ModeType? {
        val mode = spUtils.getPreference(CURRENT_MODE,0)
        return ModeType.getDeviceMode(mode)
    }

    private fun saveMode(mode: ModeType) {
        spUtils.savePreference(CURRENT_MODE,mode.type)
    }

    private fun notifyModeChange(newMode: ModeType) {
        _currentModeLiveData.value = newMode
    }
}