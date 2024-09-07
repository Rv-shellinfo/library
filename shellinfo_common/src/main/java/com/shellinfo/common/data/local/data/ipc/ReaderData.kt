package com.shellinfo.common.data.local.data.ipc

import android.os.Parcelable
import com.shellinfo.common.utils.IPCConstants.STYL_NO_ERROR
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReaderData(
    var cardData:CardData?= null,
    var terminalData: TerminalData? =null,
    var errorCode:Int? = STYL_NO_ERROR,
    var errorMessage:String ?= ""
):Parcelable
