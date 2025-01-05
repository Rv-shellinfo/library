package com.shellinfo.common.data.remote.response.model.ticket

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TicketRequest(

    @Json(name ="merchantOrderId") var merchantOrderId: String?="",
    @Json(name ="merchantId") var merchantId: String,
    @Json(name ="transType") var transType: Int?=0,
    @Json(name ="transTypeId") var transTypeId: String?="",
    @Json(name ="transDate") var transDate: String?="",
    @Json(name ="fromStationId") var fromStationId: String,
    @Json(name ="toStationid") var toStationid: String,
    @Json(name ="ticketTypeId") var ticketTypeId: Int,
    @Json(name ="noOfTickets") var noOfTickets: Int,
    @Json(name ="travelDateTime") var travelDateTime: String?="",
    @Json(name ="merchantEachTicketFareBeforeGst") var merchantEachTicketFareBeforeGst: String?="0",
    @Json(name ="merchantEachTicketFareAfterGst") var merchantEachTicketFareAfterGst: String?="0",
    @Json(name ="merchantTotalFareBeforeGst") var merchantTotalFareBeforeGst: String?="0",
    @Json(name ="merchantTotalCgst") var merchantTotalCgst: String?="0",
    @Json(name ="merchantTotalSgst") var merchantTotalSgst: String?="0",
    @Json(name ="merchantTotalFareAfterGst") var merchantTotalFareAfterGst: String?="0",
    @Json(name ="ltmrhlPassId") var ltmrhlPassId: String? ="",
    @Json(name ="patronPhoneNumber") var patronPhoneNumber: String,
    @Json(name ="fareQuoteIdforOneTicket") var fareQuoteIdforOneTicket: String?="0",
    @Json(name ="equipmentId") var equipmentId: Int?=null,
    @Json(name ="shiftId") var shiftId: Int?=null,
    @Json(name ="tomEFOUserId") var tomEFOUserId: Int?=null,
    @Json(name ="ticketIssueStationId") var ticketIssueStationId: Int?=null,
    @Json(name ="cashEnterAmount") var cashEnterAmount: Int?=null,
    @Json(name ="cashChangeAmount") var cashChangeAmount: Int?=null,
    @Json(name ="paymentMode") var paymentMode: Int?=null,
    @Json(name ="paymentChannel") var paymentChannel: Int?=null,
    @Json(name ="paymentGateway") var paymentGateway: String?=null,
    @Json(name ="operatorId") var operatorId: String?=null,
    @Json(name ="ltmrhlPurchaseId") var ltmrhlPurchaseId: String?="",
)
