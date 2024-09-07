package com.shellinfo.common.data.local.data.ipc

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class RupayCardDataRead(
    val TAG_5A:String,
    val TAG_95:String,
    val TAG_DF33:String,
    val TAG_5F25:String,
    val TAG_57:String,
    val cardType:String,
    val errorCode:Int
): Parcelable