package com.shellinfo.common

import android.os.Parcelable
import com.shellinfo.common.data.local.data.ipc.base.GenericParceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith

@Parcelize
class BaseMessage<T>(
    val messageId: Int,
    val data: @WriteWith<GenericParceler> T
): Parcelable
