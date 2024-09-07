package com.shellinfo.common.data.local.data.ipc

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EmvCardDataRead(
    val TAG_5A:String,
    val TAG_95:String,
    val TRX_STATUS:String
): Parcelable
