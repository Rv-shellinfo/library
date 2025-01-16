package com.shellinfo.common.data.local.data.serial

import android.os.Parcel
import android.os.Parcelable

data class SerialMessage(val byteArray: ByteArray) : Parcelable {

    // Write data to the Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByteArray(byteArray) // Write the ByteArray
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<SerialMessage> {
        // Create an instance from the Parcel
        override fun createFromParcel(parcel: Parcel): SerialMessage {
            val byteArray = parcel.createByteArray() ?: ByteArray(0) // Read the ByteArray
            return SerialMessage(byteArray)
        }

        override fun newArray(size: Int): Array<SerialMessage?> = arrayOfNulls(size)
    }
}
