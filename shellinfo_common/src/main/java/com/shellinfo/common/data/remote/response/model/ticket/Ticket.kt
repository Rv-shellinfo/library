package com.shellinfo.common.data.remote.response.model.ticket

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Ticket(

    @Json(name ="ticketId") val ticketId: String,
    @Json(name ="rjtID") val rjtID: String?,
    @Json(name ="ticketContent") val ticketContent: String?,
    @Json(name ="fromStationId") val fromStationId: String?,
    @Json(name ="toStationId") val toStationId: String?,
    @Json(name ="ticketTypeId") val ticketTypeId: String?,
    @Json(name ="ticketStatus") val ticketStatus: String?,
    @Json(name ="noOfTripsRemaining") val noOfTripsRemaining: String?,
    @Json(name ="noOfTripsUsed") val noOfTripsUsed: String?,
    @Json(name ="remainingStoredValue") val remainingStoredValue: String?,
    @Json(name ="entryExitType") val entryExitType: String?,
    @Json(name ="ticketExpiryTime") val ticketExpiryTime: String?,
    @Json(name ="cashEnterAmount") val cashEnterAmount: String?,
    @Json(name ="cashChangeAmount") val cashChangeAmount: String?,
    @Json(name ="platFormNo") val platFormNo: String?,
    @Json(name ="ticketPurchaseDateTime") val ticketPurchaseDateTime: String?
)
