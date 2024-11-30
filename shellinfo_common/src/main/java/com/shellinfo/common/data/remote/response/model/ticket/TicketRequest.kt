package com.shellinfo.common.data.remote.response.model.ticket

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TicketRequest(

    @Json(name ="merchantOrderId") val merchantOrderId: String,
    @Json(name ="merchantId") val merchantId: String,
    @Json(name ="transType") val transType: Int?=0,
    @Json(name ="fromStationId") val fromStationId: String,
    @Json(name ="toStationid") val toStationid: String,
    @Json(name ="ticketTypeId") val ticketTypeId: Int,
    @Json(name ="noOfTickets") val noOfTickets: String,
    @Json(name ="travelDateTime") val travelDateTime: String?="",
    @Json(name ="merchantEachTicketFareBeforeGst") val merchantEachTicketFareBeforeGst: String?="0",
    @Json(name ="merchantEachTicketFareAfterGst") val merchantEachTicketFareAfterGst: String?="0",
    @Json(name ="merchantTotalFareBeforeGst") val merchantTotalFareBeforeGst: String?="0",
    @Json(name ="merchantTotalCgst") val merchantTotalCgst: String?="0",
    @Json(name ="merchantTotalSgst") val merchantTotalSgst: String?="0",
    @Json(name ="merchantTotalFareAfterGst") val merchantTotalFareAfterGst: String?="0",
    @Json(name ="ltmrhlPassId") val ltmrhlPassId: String? ="",
    @Json(name ="patronPhoneNumber") val patronPhoneNumber: String,
    @Json(name ="fareQuoteIdforOneTicket") val fareQuoteIdforOneTicket: String?="0",
    @Json(name ="equipmentId") val equipmentId: Int?=null,
    @Json(name ="shiftId") val shiftId: Int?=null,
    @Json(name ="tomEFOUserId") val tomEFOUserId: Int?=null,
    @Json(name ="ticketIssueStationId") val ticketIssueStationId: Int?=null,
    @Json(name ="cashEnterAmount") val cashEnterAmount: Int?=null,
    @Json(name ="cashChangeAmount") val cashChangeAmount: Int?=null,
    @Json(name ="paymentMode") val paymentMode: Int?=null,
    @Json(name ="paymentChannel") val paymentChannel: Int?=null,
    @Json(name ="paymentGateway") val paymentGateway: String?=null,
    @Json(name ="operatorId") val operatorId: String?=null,
    @Json(name ="ltmrhlPurchaseId") val ltmrhlPurchaseId: String?="",
)
