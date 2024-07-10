package com.shellinfo.common.code

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashfree.pg.api.CFPaymentGatewayService
import com.cashfree.pg.core.api.callback.CFCheckoutResponseCallback
import com.cashfree.pg.core.api.utils.CFErrorResponse
import com.shellinfo.common.code.enums.ApiMode
import com.shellinfo.common.code.enums.PaymentGatewayType
import com.shellinfo.common.code.enums.TicketType
import com.shellinfo.common.code.payment_gateway.CashFreePaymentGateway
import com.shellinfo.common.code.payment_gateway.PaymentGateway
import com.shellinfo.common.code.payment_gateway.PaymentProcessor
import com.shellinfo.common.data.local.db.entity.OrdersTable
import com.shellinfo.common.data.local.db.entity.StationsTable
import com.shellinfo.common.data.local.db.entity.TicketBackupTable
import com.shellinfo.common.data.local.db.repository.DbRepository
import com.shellinfo.common.data.remote.repository.ApiRepository
import com.shellinfo.common.data.remote.response.ApiResponse
import com.shellinfo.common.data.remote.response.model.fare.FareRequest
import com.shellinfo.common.data.remote.response.model.fare.FareResponse
import com.shellinfo.common.data.remote.response.model.payment_gateway.AppPaymentRequest
import com.shellinfo.common.data.remote.response.model.payment_gateway.AppPaymentResponse
import com.shellinfo.common.data.remote.response.model.payment_gateway.ChecksumRequest
import com.shellinfo.common.data.remote.response.model.payment_gateway.TrackTransactionRequest
import com.shellinfo.common.data.remote.response.model.server.ServerDateTimeResponse
import com.shellinfo.common.data.remote.response.model.ticket.TicketRequest
import com.shellinfo.common.data.remote.response.model.ticket.TicketResponse
import com.shellinfo.common.utils.DateUtils
import com.shellinfo.common.utils.Utils
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class NetworkCall @Inject constructor(
    private val apiRepository: ApiRepository,
    private val dbRepository: DbRepository,
    private val moshi: Moshi
):ViewModel(){


    //station list
    var stationList= mutableListOf<StationsTable>()

    //fare mutable live data
    private val _fareData = MutableLiveData<ApiResponse<List<FareResponse>>>()
    val fareLiveData: LiveData<ApiResponse<List<FareResponse>>> get() = _fareData


    //book ticket live data
    private val _bookTicket = MutableLiveData<ApiResponse<TicketResponse>>()
    val bookTicketLiveData: LiveData<ApiResponse<TicketResponse>> get() = _bookTicket


    //sever date time response
    private val _serverDateTime = MutableLiveData<ApiResponse<ServerDateTimeResponse>>()
    val severDateTimeLiveData : LiveData<ApiResponse<ServerDateTimeResponse>> get() = _serverDateTime

    //payment gateway response handler
    val _paymentGatewayResponse = MutableLiveData<ApiResponse<AppPaymentResponse>>()
    val paymentGatewayLiveData : LiveData<ApiResponse<AppPaymentResponse>> get() = _paymentGatewayResponse


    /**
     * Station list getting
     */
    fun fetchStationsList(){
        viewModelScope.launch {
            apiRepository.fetchStations().collect(){ response ->
                when(response){

                    is ApiResponse.Loading -> {

                    }
                    is ApiResponse.Success ->{
                        if(response.data.returnCode==200){

                            //clear list
                            stationList.clear()

                            //loop api station list
                            response.data.stations?.forEach { station ->

                                stationList.add( StationsTable(
                                    stationId = station.stationId,
                                    name = station.name,
                                    shortName = station.shortName,
                                    corridorId = station.corridorId,
                                    corridorName = station.corridorName,
                                    latitude = station.lattitude,
                                    longitude=station.longitude,
                                    stationName = station.stationName,
                                    isJunction = station.isJunction,
                                    routeColorCode = station.routeColorCode,
                                    mstId = station.mstId,
                                    status = station.status
                                ))
                            }

                            //save in the database
                            dbRepository.insertStations(stationList)
                        }
                    }

                    is ApiResponse.Error ->{

                    }
                    else ->{

                    }
                }

            }
        }

    }

    /**
     * Station list getting for public mode
     */
    fun fetchStationsListPublic(){
        viewModelScope.launch {
            apiRepository.fetchStationsPublic().collect(){ response ->
                when(response){

                    is ApiResponse.Loading -> {

                    }
                    is ApiResponse.Success ->{

                        if(response.data.returnMsg.equals("SUCESS")){


                            //clear list
                            stationList.clear()

                            //loop api station list
                            response.data.stations?.forEach { station ->

                                stationList.add( StationsTable(
                                    stationId = station.stationId,
                                    name = station.name,
                                    shortName = station.shortName,
                                    corridorId = station.corridorId,
                                    corridorName = station.corridorName,
                                    latitude = station.lattitude,
                                    longitude=station.longitude,
                                    stationName = station.stationName,
                                    isJunction = station.isJunction,
                                    routeColorCode = station.routeColorCode,
                                    mstId = station.mstId,
                                    status = station.status
                                ))
                            }

                            //save in the database
                            dbRepository.insertStations(stationList)

                        }
                    }

                    is ApiResponse.Error ->{

                    }
                    else ->{

                    }
                }

            }
        }

    }

    /**
     * method to fetch the fare
     */
    fun fetchFare(fareRequest:FareRequest,apiMode: ApiMode){
        viewModelScope.launch {
            apiRepository.getFare(fareRequest,apiMode).collect(){
                response -> _fareData.value=response
            }
        }
    }

    /**
     * method to book the ticket
     */
    fun bookTicket(ticketRequest: TicketRequest,apiMode: ApiMode){
        viewModelScope.launch {

            //calculate per ticket amount
            var ticketPricePerPerson ="0";

            if(ticketRequest.ticketTypeId.equals(TicketType.SJT.type)){
                ticketPricePerPerson= ticketRequest.merchantEachTicketFareAfterGst!!
            }else if(ticketRequest.ticketTypeId.equals(TicketType.RJT.type)){
                ticketPricePerPerson= ((ticketRequest.merchantEachTicketFareAfterGst!!.toInt() / 2).toString())
            }

            //create order data
            val orderData= OrdersTable(
                purchaseId = ticketRequest.merchantOrderId,
                fromStation = dbRepository.getStationById(ticketRequest.fromStationId).stationName!!,
                toStation =  dbRepository.getStationById(ticketRequest.toStationid).stationName!!,
                unitPrice = ticketPricePerPerson,
                transDate =  DateUtils.getTimerText()
            )

            //save order data
            dbRepository.insertOrder(orderData)


            apiRepository.getQrTicket(ticketRequest,apiMode).collect{ response->

                when(response){

                    is ApiResponse.Loading -> {
                        _bookTicket.value= response
                    }

                    is ApiResponse.Success ->{

                        //getting response body
                        val res= response.data

                        //check return code
                        if(res.returnCode.equals("0")){
                            if(res.tickets.size>0){


                                res.tickets.forEach {

                                    //save ticket in database
                                    val ticketData = TicketBackupTable(
                                        shiftId = ""+ticketRequest.shiftId,
                                        operatorId = ticketRequest.operatorId,
                                        fromStation = dbRepository.getStationById(ticketRequest.fromStationId).stationName,
                                        toStationName = dbRepository.getStationById(ticketRequest.toStationid).stationName,
                                        unitPrice = ticketPricePerPerson,
                                        totalFare = ticketPricePerPerson,
                                        purchaseId = ticketRequest.merchantOrderId,
                                        ticketId = it.ticketId,
                                        jType = it.ticketTypeId,
                                        passengerMoney = ""+ticketRequest.cashEnterAmount,
                                        changeMoney = ""+ticketRequest.cashChangeAmount,
                                        transDate = DateUtils.getDate("yyyy-MM-dd HH:mm:ss"),
                                        transTime = "",
                                        paymentMode = ""+ticketRequest.paymentMode,
                                        noOfTickets = "1"
                                    )

                                    //save in the table
                                    dbRepository.insertTicket(ticketData)


                                    //TODO print ticket functionality needs to add
                                    //TODO save in the log file functionality needs to add

                                    //delete order from table
                                    dbRepository.deleteOrderByPurchaseId(ticketRequest.merchantOrderId)


                                    _bookTicket.value= response

                                }

                            }
                        }else{
                            _bookTicket.value= response
                        }

                    }

                    is ApiResponse.Error ->{
                        _bookTicket.value= response
                    }

                    else ->{
                        _bookTicket.value= response
                    }
                }
            }
        }
    }

    /**
     * get server date time
     */
    fun getServerDateTime(apiMode: ApiMode){

        viewModelScope.launch {

            apiRepository.getTicketBookTiming(apiMode).collect{ response->

                _serverDateTime.value= response
            }
        }
    }


    /**
     * method to validate transaction details before
     * starting payment gateway flow
     */
    fun validateTransactionDetail(apiMode: ApiMode,request: AppPaymentRequest, context: Activity){

        viewModelScope.launch {

            // generate order id
            val orderId = Utils.generateOrderId(request.appVersionName)

            // request for valid transaction api
            val transactionVerificationRequest = TrackTransactionRequest(
                fareQuoteId = request.fareQuoteId,
                custId = request.customer_id,
                custName = request.customer_name,
                email = request.customer_email,
                mobileNo = request.customer_mobile,
                orderDate = DateUtils.getDateInSpecificFormat("yyyy-MM-dd HH:mm:ss")!!,
                gatewayName = request.paymentGateway.name,
                txnAmount = request.totalAmount,
                tSavariOrderId = orderId,
                appversion = request.appVersionName,
                noOfTickets = request.noOfTickets,
            )

            // call transaction validation api
            apiRepository.trackTransactionRequest(apiMode,transactionVerificationRequest)
                .collect{ response ->

                    when(response){

                        is ApiResponse.Loading ->{
                            _paymentGatewayResponse.value= response
                        }

                        is ApiResponse.Success ->{
                            if(response.data.returnMsg.equals("Success")){

                                //set orderId
                                request.orderId=orderId

                                //if success start checksum request
                                generatePaymentGatewayChecksum(request,context)

                            }else{

                                _paymentGatewayResponse.value=ApiResponse.Error(response.data.returnMsg, Exception("Transaction Validation Error"))

                            }
                        }

                        is ApiResponse.Error ->{
                            _paymentGatewayResponse.value=response
                        }

                        else ->{
                            _paymentGatewayResponse.value=ApiResponse.Error("error", Exception("Transaction Validation error"))
                        }


                    }

            }
        }
    }

    /**
     * method to generate payment gateway checksum
     */
    fun generatePaymentGatewayChecksum(payRequest: AppPaymentRequest, context: Activity){

        //create request
        val request = ChecksumRequest(orderid = payRequest.orderId!!,amount = payRequest.totalAmount)

        viewModelScope.launch {

            apiRepository.generatePaymentGatewayChecksum(request).collect{ response ->

                when(response){

                    is ApiResponse.Loading ->{

                    }

                    is ApiResponse.Success ->{
                        if(response.data.s.equals("y")){

                            //get checksum string
                            val checksum=  response.data.dtls.checksum

                            //set check sum
                            payRequest.checksum= checksum

                            //start payment gateway transaction
                            doPaymentTransaction(payRequest,context)

                        }else{
                            _paymentGatewayResponse.value=ApiResponse.Error("error", Exception("Checksum error"))
                        }
                    }

                    is ApiResponse.Error ->{
                        _paymentGatewayResponse.value=response
                    }

                    else ->{
                        _paymentGatewayResponse.value=ApiResponse.Error("error", Exception("Checksum error"))
                    }
                }
            }
        }

    }

    /**
     * method to decide the payment gateway based on the application input
     */
    fun doPaymentTransaction(payRequest: AppPaymentRequest, context: Activity){

        //decide payment gateway based on the app input
        val paymentGateway : PaymentGateway = when (payRequest.paymentGateway){
            PaymentGatewayType.CASHFREE ->
                CashFreePaymentGateway(apiRepository, this, moshi)
        }

        //init payment processor
        val paymentProcessor= PaymentProcessor(paymentGateway)


        //call api
        paymentProcessor.processPayment(payRequest, context)

    }
}