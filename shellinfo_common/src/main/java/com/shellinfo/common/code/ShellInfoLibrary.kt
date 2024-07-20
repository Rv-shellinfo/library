package com.shellinfo.common.code

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.LiveData
import com.cashfree.pg.api.CFPaymentGatewayService
import com.cashfree.pg.core.api.callback.CFCheckoutResponseCallback
import com.cashfree.pg.core.api.utils.CFErrorResponse
import com.shellinfo.common.code.enums.ApiMode
import com.shellinfo.common.code.enums.HttpType
import com.shellinfo.common.code.enums.PrinterType
import com.shellinfo.common.code.logs.LoggerImpl
import com.shellinfo.common.code.mqtt.MQTTManager
import com.shellinfo.common.code.payment_gateway.PaymentProcessor
import com.shellinfo.common.code.printer.PrinterActions
import com.shellinfo.common.code.printer.PrinterProcessor
import com.shellinfo.common.code.printer.SunmiPrinter
import com.shellinfo.common.data.local.db.entity.StationsTable
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import com.shellinfo.common.data.remote.response.ApiResponse
import com.shellinfo.common.data.remote.response.model.fare.FareRequest
import com.shellinfo.common.data.remote.response.model.fare.FareResponse
import com.shellinfo.common.data.remote.response.model.payment_gateway.AppPaymentRequest
import com.shellinfo.common.data.remote.response.model.payment_gateway.AppPaymentResponse
import com.shellinfo.common.data.remote.response.model.server.ServerDateTimeResponse
import com.shellinfo.common.data.remote.response.model.ticket.Ticket
import com.shellinfo.common.data.remote.response.model.ticket.TicketRequest
import com.shellinfo.common.data.remote.response.model.ticket.TicketResponse
import com.shellinfo.common.utils.BarcodeUtils
import com.shellinfo.common.utils.DateUtils
import com.shellinfo.common.utils.SpConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class ShellInfoLibrary @Inject constructor(
    @ApplicationContext private val context: Context,
    private val spUtils:SharedPreferenceUtil,
    private val networkCall: NetworkCall,
    private val databaseCall: DatabaseCall,
    private val barcodeUtils: BarcodeUtils,
    private val loggerImpl: LoggerImpl,
    private val mqttManager: MQTTManager
) :ShellInfoProvider {

    private var activity: Activity? = null

    //stations live data
    var stationsLiveData: LiveData<List<StationsTable>> = databaseCall.stationsLiveData

    //single station live data
    var singleStationLiveData : LiveData<StationsTable> = databaseCall.singleStationLiveData

    //fare live data
    val fareLiveData: LiveData<ApiResponse<List<FareResponse>>> get() = networkCall.fareLiveData

    //book ticket data
    val bookTicketLiveData: LiveData<ApiResponse<TicketResponse>> get() = networkCall.bookTicketLiveData

    //server date time data
    val serverDateTime : LiveData<ApiResponse<ServerDateTimeResponse>> get() = networkCall.severDateTimeLiveData

    //payment gateway response observer
    val paymentGatewayResponse: LiveData<ApiResponse<AppPaymentResponse>> get() = networkCall.paymentGatewayLiveData


    override fun setApiMode(mode: ApiMode) {

        //set api mode
        spUtils.savePreference(SpConstants.API_MODE,mode.name)
    }

    override fun setBaseUrl(baseUrl: String) {

        //set base url
        spUtils.savePreference(SpConstants.API_BASE_URL,baseUrl)
    }

    override fun setPort(port: String) {

        //set port
        spUtils.savePreference(SpConstants.API_PORT,port)
    }

    override fun seAuthToken(token: String) {

        //set auth token
        spUtils.savePreference(SpConstants.API_TOKEN,token)
    }

    override fun setActivity(activity: Activity) {
        this.activity=activity
    }

    override fun setHttpProtocol(type: HttpType) {

        //set http types
        spUtils.savePreference(SpConstants.API_HTTP_TYPE,type.protocol)

    }

    override fun init() {

        //check if private mode or public
        if(spUtils.getPreference(SpConstants.API_MODE, ApiMode.DEFAULT.name).equals(ApiMode.PRIVATE.name)){

            //if private mode then check port and base url provided or not
            if(spUtils.getPreference(SpConstants.API_PORT,"").equals("")){
                Toast.makeText(context,"Please set PORT number for private api", Toast.LENGTH_LONG).show()
            }else if(spUtils.getPreference(SpConstants.API_BASE_URL,"").equals("")){
                Toast.makeText(context,"Please set BASE URL", Toast.LENGTH_LONG).show()
            }else{

                //create base url
                val baseUrl = "http://"+spUtils.getPreference(SpConstants.API_BASE_URL,"")+":"+spUtils.getPreference(SpConstants.API_PORT,"")

                //save base url in sp
                spUtils.savePreference(SpConstants.API_FULL_BASE_URL,baseUrl)
            }
        }else if(spUtils.getPreference(SpConstants.API_MODE, ApiMode.DEFAULT.name).equals(ApiMode.PUBLIC.name)){

            if(spUtils.getPreference(SpConstants.API_TOKEN,"").equals(""))       {
                Toast.makeText(context,"Please set AUTH Token for public mode setup", Toast.LENGTH_LONG).show()
            }else{

                //save base url in sp
                spUtils.savePreference(SpConstants.API_FULL_BASE_URL,spUtils.getPreference(SpConstants.API_BASE_URL,""))
            }
        }

        //logging start
        loggerImpl.initLogger()
        loggerImpl.startLogging()



    }



    override fun setStations() {
        if(spUtils.getPreference(SpConstants.API_MODE, ApiMode.DEFAULT.name).equals(ApiMode.PRIVATE.name)){
            networkCall.fetchStationsList()
        }else if(spUtils.getPreference(SpConstants.API_MODE, ApiMode.DEFAULT.name).equals(ApiMode.PUBLIC.name)){
            networkCall.fetchStationsListPublic()
        }else{
            Toast.makeText(context,"Please set API MODE", Toast.LENGTH_LONG).show()
        }
    }

    override fun getStations() {
        databaseCall.getAllStations()
    }

    override fun getStationsByCorridorId(id: Int) {
        databaseCall.getStationsByCorridorId(id)
    }

    override fun getStationsByCorridorName(name: String) {
        databaseCall.getStationsByCorridorName(name)
    }

    override fun searchStations(keyword: String) {
        databaseCall.searchStation(keyword)
    }

    override fun getStationByStationId(id: String) {
        databaseCall.getStationByStationId(id)
    }

    override fun getFare(fareRequest: FareRequest) {
        networkCall.fetchFare(fareRequest, ApiMode.valueOf(spUtils.getPreference(SpConstants.API_MODE,"")))
    }

    override fun generateTicket(ticketRequest: TicketRequest) {
        networkCall.bookTicket(ticketRequest,ApiMode.valueOf(spUtils.getPreference(SpConstants.API_MODE,"")))
    }

    override fun getBarcode(ticket: Ticket,width:Int, height:Int): Bitmap {
        return barcodeUtils.generateQrCode(ticket, width, height)
    }

    override fun getServerDateTime() {
         networkCall.getServerDateTime(ApiMode.valueOf(spUtils.getPreference(SpConstants.API_MODE,"")))
    }

    override fun isTicketBookDateTimeValid(
        startTime: String,
        endTime: String
    ): Boolean {
        return DateUtils.checkOperationalTimings(startTime,endTime,context)
    }

    override fun doPayment(appPaymentRequest: AppPaymentRequest) {
        networkCall.validateTransactionDetail(ApiMode.valueOf(spUtils.getPreference(SpConstants.API_MODE,"")), appPaymentRequest, activity!!)
    }

    override fun getPrinter(type: PrinterType): PrinterProcessor {

        val printer: PrinterActions = when(type){

            PrinterType.SUNMI -> SunmiPrinter()
        }


        //create object of processor based on the printer type
        val printerProcessor= PrinterProcessor(printer)

        //init the printer
        printerProcessor.initPrinterService(context)

        //returning the printer processor
        return printerProcessor

    }

    override fun initLogger() {
        loggerImpl.initLogger()
    }

    override fun startLogging() {
        loggerImpl.startLogging()
    }

    override fun stopLogging() {
        loggerImpl.stopLogging()
    }

    override fun log(tag: String, message: String) {
        loggerImpl.logData(tag,message)
    }

    override fun mqttConnect() {
        mqttManager.connect()
    }

    override fun subscribeMqttTopic(topic: String) {
        mqttManager.subscribe(topic)
    }

    override fun unsubscribeMqttTopic(topic: String) {
        mqttManager.unsubscribe(topic)
    }

    override fun publishMqttMessage(topic: String, msg: String) {
        mqttManager.publish(topic,msg)
    }

    override fun disconnectMqtt() {
        mqttManager.disconnect()
    }


}