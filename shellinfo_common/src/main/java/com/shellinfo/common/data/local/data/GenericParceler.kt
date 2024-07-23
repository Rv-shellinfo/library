package com.shellinfo.common.data.local.data

import android.os.Parcel
import kotlinx.parcelize.Parceler

object GenericParceler : Parceler<Any?> {
    override fun create(parcel: Parcel): Any? {
        return parcel.readValue(ClassLoader.getSystemClassLoader())
    }

    override fun Any?.write(parcel: Parcel, flags: Int) {
        parcel.writeValue(this)
    }
}