package com.shellinfo.common.data.local.data.ipc.base

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith

@JsonClass(generateAdapter = true)
class BaseMessage<T>(
    val messageId: Int,
    val data:  T
)
