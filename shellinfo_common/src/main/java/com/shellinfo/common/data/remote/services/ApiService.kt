package com.shellinfo.common.data.remote.services

import com.shellinfo.common.data.remote.ApiEndPoints
import com.shellinfo.common.data.remote.response.model.fare.FareRequest
import com.shellinfo.common.data.remote.response.model.fare.FareResponse
import com.shellinfo.common.data.remote.response.model.payment_gateway.AppPaymentRequest
import com.shellinfo.common.data.remote.response.model.payment_gateway.AppPaymentResponse
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
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {

    @GET(ApiEndPoints.ENDPOINT_GET_STATIONS)
    suspend fun getStations(@Url url: String): Response<StationsResponse>

    @POST
    suspend fun getStationsPublic(
        @Url url: String,
        @Body body: StationRequest,

        ): Response<StationsResponse>


    @POST
    suspend fun getFare(@Url url: String,@Body body: FareRequest): Response<List<FareResponse>>

    @POST
    suspend fun getTicket(@Url url: String,@Body body:TicketRequest):Response<TicketResponse>

    @POST
    suspend fun checkTicketBookTimeValidPublic(@Url url: String,@Body body: ServerDateTimeRequest):Response<ServerDateTimeResponse>

    @GET
    suspend fun checkTicketBookTimeValidPrivate(@Url url: String,@Body body: ServerDateTimeRequest):Response<ServerDateTimeResponse>

    @POST
    suspend fun validateTransactionDetails(@Url url: String, @Body body: TrackTransactionRequest):Response<TrackTransactionResponse>

    @POST
    suspend fun generatePaymentGatewayChecksum(@Url url: String, @Body body:ChecksumRequest):Response<ChecksumResponse>

    @POST
    suspend fun doCashFreePaymentInitCall(
        @Url url: String,
        @Header("x-api-version") version:String,
        @Header("x-client-id") clientId:String,
        @Header("x-client-secret") secret:String,
        @Body body:CashFreePaymentRequest):Response<CashFreePaymentResponse>

    @POST
    suspend fun getTicketByOrderId(@Url url: String, @Body body:OrderStatusRequest):Response<TicketResponse>
}