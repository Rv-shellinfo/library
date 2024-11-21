package com.shellinfo.common.data.remote

object ApiEndPoints {

    const val BASE_URL_1= "https://maas.ts-afc.com"

    //private endpoints
    const val ENDPOINT_GET_STATIONS =  "/api/pos/v1/Station/getStations"
    const val ENDPOINT_GET_FARE =  "/api/pos/v1/Fare/GetFare"
    const val ENDPOINT_CREATE_E_TICKET =  "/api/v1/BilleasyPayload/CreateETicket"
    const val ENDPOINT_GENERATE_TICKET =  "/api/pos/v1/Ticket/GenerateTicket"
    const val ENDPOINT_GET_BUSINESS_OPERATIONAL_DATE_TIME =  "/api/v2/Operation/GetBusinessOperationDateHour"
    const val ENDPOINT_GENERATE_PAID_FREE_EXIT_TICKET =  "/api/pos/v1/Ticket/GeneratePaidFreeExitTicket"
    const val ENDPOINT_CHANGE_DESTINATION_PREVIEW =  "/api/pos/v1/ChangeDestination/POSChangeOfDestinationPreview"
    const val ENDPOINT_CHANGE_DESTINATION_CONFIRM =  "/api/pos/v1/ChangeDestination/POSChangeOfDestinationConfirm"
    const val ENDPOINT_TICKET_DETAILS_BY_ID =  "/api/pos/v1/Ticket/GetPOSTicketDetailsById"
    const val ENDPOINT_TICKET_DETAILS_BY_CONTENT =  "/api/pos/v1/Ticket/GetPOSTicketDetailsByContent"
    const val ENDPOINT_TICKET_DETAILS_BY_PHONE =  "/api/pos/v1/Ticket/GetPOSTicketDetailsByPatronPhone"
    const val ENDPOINT_GET_QR_FARE_SPECIAL_EXIST =  "/api/pos/v1/Fare/getQRFareSpecialExit"
    const val ENDPOINT_BILL_EASY_OTP_VERIFICATION =  "/api/v1/Billeasy/OTPVerification"
    const val ENDPOINT_ENTRY_VALIDATION =  "/api/v1/Transaction/EntryValidation"
    const val ENDPOINT_HEALTH_URL =  "/api/v1/Equipment/UpdateEquipHealthData"
    const val ENDPOINT_ENTRY_TRX_URL =  "/api/v1/Transaction/AddEntryTxnDetails"
    const val ENDPOINT_EXIT_TRX_URL =  "/api/v1/Transaction/AddExitTxnDetails"
    const val ENDPOINT_STATIONS =  "/api/v1/Station/GetStations"
    const val ENDPOINT_DAILY_LIMITS =  "/api/v1/Pass/Dailylimits"
    const val ENDPOINT_TRIP_LIMITS =  "/api/v1/Pass/Triplimits"
    const val ENDPOINT_ZONES =  "/api/v1/Zone/GetZoneList"
    const val ENDPOINT_PASS_TYPES =  "/api/v1/Pass/ProductTypes"
    const val ENDPOINT_PURCHASE_PASS =  "/api/v1/Pass/PurchacePass"




    //public endpoints
    const val PUBLIC_ENDPOINT_GET_STATIONS =  "getstationsqr/services.do"
    const val PUBLIC_ENDPOINT_GET_FARE =  "getfareforqr/services.do"
    const val PUBLIC_ENDPOINT_GENERATE_TICKET =  "generateqrticket/services.do"
    const val PUBLIC_ENDPOINT_GET_BUSINESS_OPERATIONAL_DATE_TIME =  "GetBusinessOperationDateHour/services.do"
    const val PUBLIC_TRACK_TRANSACTION_DETAILS =  "tracktxndetailsqr/services.do"
    const val PUBLIC_GENERATE_PAYMENT_GATEWAY_CHECKSUM =  "getbilldeskchecksumandroidnewqr/services.do"
    const val PUBLIC_CASH_FREE_PAYMENT_API =  "https://sandbox.cashfree.com/pg/orders"
    const val PUBLIC_GENERATE_TICKET_BY_ORDER_ID =  "getorderstatusbytsavaariorderid/services.do"

}