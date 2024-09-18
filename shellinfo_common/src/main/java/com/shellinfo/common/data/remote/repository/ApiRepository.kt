package com.shellinfo.common.data.remote.repository

import com.shellinfo.common.code.enums.ApiMode
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import com.shellinfo.common.data.remote.ApiEndPoints
import com.shellinfo.common.data.remote.NetworkUtils
import com.shellinfo.common.data.remote.response.ApiResponse
import com.shellinfo.common.data.remote.response.model.entry_validation.EntryValidationRequest
import com.shellinfo.common.data.remote.response.model.entry_validation.EntryValidationResponse
import com.shellinfo.common.data.remote.response.model.fare.FareRequest
import com.shellinfo.common.data.remote.response.model.fare.FareResponse
import com.shellinfo.common.data.remote.response.model.gate_fare.GateFareRequest
import com.shellinfo.common.data.remote.response.model.gate_fare.GateFareResponse
import com.shellinfo.common.data.remote.response.model.payment_gateway.ChecksumRequest
import com.shellinfo.common.data.remote.response.model.payment_gateway.ChecksumResponse
import com.shellinfo.common.data.remote.response.model.payment_gateway.TrackTransactionRequest
import com.shellinfo.common.data.remote.response.model.payment_gateway.TrackTransactionResponse
import com.shellinfo.common.data.remote.response.model.payment_gateway.cash_free.CashFreePaymentRequest
import com.shellinfo.common.data.remote.response.model.payment_gateway.cash_free.CashFreePaymentResponse
import com.shellinfo.common.data.remote.response.model.payment_gateway.order_status.OrderStatusRequest
import com.shellinfo.common.data.remote.response.model.server.ServerDateTimeRequest
import com.shellinfo.common.data.remote.response.model.server.ServerDateTimeResponse
import com.shellinfo.common.data.remote.response.model.stations.StationRequest
import com.shellinfo.common.data.remote.response.model.stations.StationsResponse
import com.shellinfo.common.data.remote.response.model.ticket.TicketRequest
import com.shellinfo.common.data.remote.response.model.ticket.TicketResponse
import com.shellinfo.common.data.remote.services.ApiService
import com.shellinfo.common.utils.SpConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ApiRepository @Inject constructor(
    private val apiService: ApiService,
    private val networkUtils: NetworkUtils,
    private val spUtils: SharedPreferenceUtil){


    private val URL = spUtils.getPreference(SpConstants.API_FULL_BASE_URL,"")
    private val TOKEN = spUtils.getPreference(SpConstants.API_TOKEN,"")

    /**
     * method to fetch the stations in private mode
     */
    fun fetchStations(): Flow<ApiResponse<StationsResponse>> = flow {
        emit(ApiResponse.Loading)
        emit(networkUtils.handleApiCall { apiService.getStations(URL) })
    }

    /**
     * method to fetch the stations in public mode
     */
    fun fetchStationsPublic(): Flow<ApiResponse<StationsResponse>> = flow {
        emit(ApiResponse.Loading)
        emit(networkUtils.handleApiCall { apiService.getStationsPublic(URL+ApiEndPoints.PUBLIC_ENDPOINT_GET_STATIONS, StationRequest(TOKEN)) })
    }


    /**
     * method to fetch the fare based on the from and to stations
     */
    fun getFare(fareRequest: FareRequest, apiMode: ApiMode): Flow<ApiResponse<List<FareResponse>>> = flow {
        emit(ApiResponse.Loading)

        var apiEndPoint=""
        //check api mode
        if(apiMode.type.equals("private")){
            apiEndPoint=URL+ApiEndPoints.ENDPOINT_GET_FARE
        }else if(apiMode.type.equals("public")){
            apiEndPoint=URL+ApiEndPoints.PUBLIC_ENDPOINT_GET_FARE
            fareRequest.authorization=spUtils.getPreference(SpConstants.API_TOKEN,"")
        }

        emit(networkUtils.handleApiCall { apiService.getFare(apiEndPoint,fareRequest) })
    }


    /**
     * method to get the booked ticket
     */
    fun getQrTicket(ticketRequest: TicketRequest, apiMode: ApiMode): Flow<ApiResponse<TicketResponse>> = flow {
        emit(ApiResponse.Loading)
        var apiEndPoint=""
        //check api mode
        if(apiMode.type.equals("private")){
            apiEndPoint=URL+ApiEndPoints.ENDPOINT_CREATE_E_TICKET
        }else if(apiMode.type.equals("public")){
            apiEndPoint=URL+ApiEndPoints.PUBLIC_ENDPOINT_GENERATE_TICKET
            ticketRequest.authorization=spUtils.getPreference(SpConstants.API_TOKEN,"")
        }

        emit(networkUtils.handleApiCall { apiService.getTicket(apiEndPoint,ticketRequest) })
    }

    /**
     * method to check from server
     * ticket booking time is valid or not
     */
    fun getTicketBookTiming(apiMode: ApiMode): Flow<ApiResponse<ServerDateTimeResponse>> = flow {
        emit(ApiResponse.Loading)
        var apiEndPoint=""
        //check api mode
        if(apiMode.type.equals("private")){
            val request = ServerDateTimeRequest()
            apiEndPoint=URL+ApiEndPoints.ENDPOINT_GET_BUSINESS_OPERATIONAL_DATE_TIME
            emit(networkUtils.handleApiCall { apiService.checkTicketBookTimeValidPrivate(apiEndPoint,request) })
        }else if(apiMode.type.equals("public")){
            apiEndPoint=URL+ApiEndPoints.PUBLIC_ENDPOINT_GET_BUSINESS_OPERATIONAL_DATE_TIME
            val request = ServerDateTimeRequest()
            request.authorization=spUtils.getPreference(SpConstants.API_TOKEN,"")
            emit(networkUtils.handleApiCall { apiService.checkTicketBookTimeValidPublic(apiEndPoint,request) })
        }
    }


    /**
     * method to validate transaction
     */
    fun trackTransactionRequest(apiMode: ApiMode, request: TrackTransactionRequest): Flow<ApiResponse<TrackTransactionResponse>> = flow {
        emit(ApiResponse.Loading)
        request.authorization=spUtils.getPreference(SpConstants.API_TOKEN,"")
        emit(networkUtils.handleApiCall { apiService.validateTransactionDetails(URL+ApiEndPoints.PUBLIC_TRACK_TRANSACTION_DETAILS,request)})
    }

    /**
     * api call to generate payment gateway checksum
     */
    fun generatePaymentGatewayChecksum(request: ChecksumRequest): Flow<ApiResponse<ChecksumResponse>> = flow {
        emit(ApiResponse.Loading)
        emit(networkUtils.handleApiCall { apiService.generatePaymentGatewayChecksum(URL+ApiEndPoints.PUBLIC_GENERATE_PAYMENT_GATEWAY_CHECKSUM,request)})
    }


    /**
     * Cash free payment init
     */
    fun doCashFreePaymentInit(request: CashFreePaymentRequest): Flow<ApiResponse<CashFreePaymentResponse>> = flow {
        emit(ApiResponse.Loading)
        emit(networkUtils.handleApiCall { apiService.doCashFreePaymentInitCall(
            ApiEndPoints.PUBLIC_CASH_FREE_PAYMENT_API,
            "2023-08-01",
            "110055bebf972ba2707eb4f09e550011",
            "9ce78473a251dd33bc0c1a301506b879d25e907f",request)})
    }

    /**
     * method to get the tickets by order id
     */
    fun getTicketByOrderId(request: OrderStatusRequest):Flow<ApiResponse<TicketResponse>> = flow {
        emit(ApiResponse.Loading)
        request.authorization=spUtils.getPreference(SpConstants.API_TOKEN,"")
        emit(networkUtils.handleApiCall { apiService.getTicketByOrderId(URL+ApiEndPoints.PUBLIC_GENERATE_TICKET_BY_ORDER_ID,request)})
    }

    fun doEntryValidation(request:EntryValidationRequest):Flow<ApiResponse<EntryValidationResponse>> = flow {
        emit(ApiResponse.Loading)
        emit(networkUtils.handleApiCall { apiService.doEntryValidation("https://maas.ts-afc.com"+ApiEndPoints.ENDPOINT_ENTRY_VALIDATION,request)})
    }

    fun doFareCalculation(request:GateFareRequest):Flow<ApiResponse<GateFareResponse>> = flow {
        emit(ApiResponse.Loading)
        emit(networkUtils.handleApiCall { apiService.doGetFare("https://maas.ts-afc.com"+ApiEndPoints.ENDPOINT_GET_FARE,request)})
    }
}