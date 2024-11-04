package com.shellinfo.common.data.local.data.emv_rupay.binary.osa_bin

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PassBin(

    var productType: Byte?= 0,                                      //1 Byte
    var passLimit: Byte? =0,                                        //1 Byte (Pass Limit or No. of Trips)
    var startDateTime: ByteArray = ByteArray(3),               //3 Bytes (Activation Date and Time)
    var endDateTime: ByteArray=ByteArray(2),                   //2 Bytes (Expiry Date and Time)
    var validZoneId: Byte? = 0,                                     //1 Byte (Valid Route or Zone ID)
    var validEntryStationId: Byte? = 0,                             //1 Byte (Valid Entry Station ID)
    var validExitStationId: Byte? = 0,                              //1 Byte (Valid Exit Station ID)
    var tripCount: Byte? = 0,                                       //1 Byte (Trip Consumed)
    var lastConsumedDate: ByteArray=ByteArray(2),              //2 Bytes (Class or Privileges)
    var dailyLimit: Byte? =0,                                       //1 Byte (Class or Privileges)
    var priority: Byte? =0,                                         //1 Byte  (Class or Privileges)
)
