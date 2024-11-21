package com.shellinfo.common.data.remote.services

import com.shellinfo.common.data.remote.response.model.pass.GetPassResponse
import com.shellinfo.common.data.remote.ApiEndPoints
import com.shellinfo.common.data.remote.response.model.daily_limit.DailyLimitResponse
import com.shellinfo.common.data.remote.response.model.entry_trx.EntryTrxRequest
import com.shellinfo.common.data.remote.response.model.entry_trx.EntryTrxResponse
import com.shellinfo.common.data.remote.response.model.entry_validation.EntryValidationRequest
import com.shellinfo.common.data.remote.response.model.entry_validation.EntryValidationResponse
import com.shellinfo.common.data.remote.response.model.exit_trx.ExitTrxRequest
import com.shellinfo.common.data.remote.response.model.exit_trx.ExitTrxResponse
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
import com.shellinfo.common.data.remote.response.model.purchase_pass.PurchasePassRequest
import com.shellinfo.common.data.remote.response.model.purchase_pass.PurchasePassResponse
import com.shellinfo.common.data.remote.response.model.server.ServerDateTimeRequest
import com.shellinfo.common.data.remote.response.model.server.ServerDateTimeResponse
import com.shellinfo.common.data.remote.response.model.stations.StationRequest
import com.shellinfo.common.data.remote.response.model.stations.StationsResponse
import com.shellinfo.common.data.remote.response.model.stations_new.StationDataResponse
import com.shellinfo.common.data.remote.response.model.ticket.TicketRequest
import com.shellinfo.common.data.remote.response.model.ticket.TicketResponse
import com.shellinfo.common.data.remote.response.model.trip_limit.TripLimitResponse
import com.shellinfo.common.data.remote.response.model.zone.ZoneDataResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
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

    @POST
    suspend fun doEntryValidation(@Url url: String, @Body body:EntryValidationRequest):Response<EntryValidationResponse>

    @POST
    suspend fun doGetFare(@Url url: String, @Body body: GateFareRequest):Response<GateFareResponse>

    @GET
    suspend fun doGetPassTypes(@Url url: String,@Query("operatorId") operatorId:String):Response<GetPassResponse>

    @GET
    suspend fun doGetTripLimits(@Url url: String,@Query("operatorId") operatorId:String):Response<TripLimitResponse>

    @GET
    suspend fun doGetDailyLimits(@Url url: String,@Query("operatorId") operatorId:String):Response<DailyLimitResponse>

    @GET
    suspend fun doGetStations(@Url url: String,@Query("operatorId") operatorId:String):Response<StationDataResponse>

    @GET
    suspend fun doGetZones(@Url url: String,@Query("operatorId") operatorId:String):Response<ZoneDataResponse>

    @POST
    suspend fun doSyncPurchasePassData(@Url url: String,@Body request:PurchasePassRequest):Response<PurchasePassResponse>

    @POST
    suspend fun doSyncEntryTrxData(@Url url: String,@Body request:EntryTrxRequest):Response<EntryTrxResponse>

    @POST
    suspend fun doSyncExitTrxData(@Url url: String,@Body request:ExitTrxRequest):Response<ExitTrxResponse>



}