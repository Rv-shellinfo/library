package com.shellinfo.common.data.local.data.ipc.base

import android.os.Parcel
import kotlinx.parcelize.Parceler

    object GenericParceler : Parceler<Any?> {
        override fun create(parcel: Parcel): Any? {
            return parcel.readValue(BaseMessage::class.java.classLoader)
        }

        override fun Any?.write(parcel: Parcel, flags: Int) {
            parcel.writeValue(this)
        }
    }