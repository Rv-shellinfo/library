package com.shellinfo.common.code

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.jakewharton.threetenabp.AndroidThreeTen
import com.shellinfo.common.BuildConfig
import com.shellinfo.common.code.enums.ApiMode
import com.shellinfo.common.code.enums.EquipmentType
import com.shellinfo.common.code.enums.HttpType
import com.shellinfo.common.code.enums.NcmcDataType
import com.shellinfo.common.code.enums.PrinterType
import com.shellinfo.common.code.ipc.IPCDataHandler
import com.shellinfo.common.code.logs.LoggerImpl
import com.shellinfo.common.code.mqtt.MQTTManager
import com.shellinfo.common.code.printer.PrinterActions
import com.shellinfo.common.code.printer.PrinterProcessor
import com.shellinfo.common.code.printer.SunmiPrinter
import com.shellinfo.common.data.local.data.InitData
import com.shellinfo.common.data.local.data.ipc.BF200Data
import com.shellinfo.common.data.local.data.ipc.ServiceInfo
import com.shellinfo.common.data.local.data.ipc.base.BaseMessage
import com.shellinfo.common.data.local.db.entity.StationsTable
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import com.shellinfo.common.data.remote.response.ApiResponse
import com.shellinfo.common.data.remote.response.model.fare.FareRequest
import com.shellinfo.common.data.remote.response.model.fare.FareResponse
import com.shellinfo.common.data.remote.response.model.pass.PassRequest
import com.shellinfo.common.data.remote.response.model.payment_gateway.AppPaymentRequest
import com.shellinfo.common.data.remote.response.model.payment_gateway.AppPaymentResponse
import com.shellinfo.common.data.remote.response.model.server.ServerDateTimeResponse
import com.shellinfo.common.data.remote.response.model.ticket.Ticket
import com.shellinfo.common.data.remote.response.model.ticket.TicketRequest
import com.shellinfo.common.data.remote.response.model.ticket.TicketResponse
import com.shellinfo.common.data.shared.SharedDataManager
import com.shellinfo.common.utils.BarcodeUtils
import com.shellinfo.common.utils.DateUtils
import com.shellinfo.common.utils.IPCConstants.MSG_ID_CREATE_OSA_SERVICE
import com.shellinfo.common.utils.IPCConstants.MSG_ID_CREATE_PASS
import com.shellinfo.common.utils.IPCConstants.MSG_ID_ONE_TIME_READ_CARD_REQUEST
import com.shellinfo.common.utils.IPCConstants.MSG_ID_REMOVE_PENALTY
import com.shellinfo.common.utils.IPCConstants.MSG_ID_START_CARD_DETECTION
import com.shellinfo.common.utils.IPCConstants.MSG_ID_STOP_CARD_DETECTION
import com.shellinfo.common.utils.PermissionsUtils
import com.shellinfo.common.utils.SpConstants
import com.shellinfo.common.utils.SpConstants.COMMON_SERVICE_ID
import com.shellinfo.common.utils.SpConstants.OPERATOR_SERVICE_ID
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import org.eclipse.paho.client.mqttv3.MqttMessage
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShellInfoLibrary @Inject constructor(
    @ApplicationContext private val context: Context,
    private val spUtils:SharedPreferenceUtil,
    private val networkCall: NetworkCall,
    private val databaseCall: DatabaseCall,
    private val barcodeUtils: BarcodeUtils,
    private val loggerImpl: LoggerImpl,
    private val mqttManager: MQTTManager,
    private val ipcDataHandler: IPCDataHandler,
    private val permissionsUtils: PermissionsUtils,
    private val sharedDataManager: SharedDataManager
) :ShellInfoProvider {

    companion object{
        var isForPenalty=false
        var isForPassCreate=false
        var isForOsaRead=false
        var isForOsaCreate=false
        var isForOsaDelete=false
        lateinit var passCreateRequest: PassRequest
    }

    //application activity context
    private var activity: AppCompatActivity? = null

    //tag for logs
    private val TAG = ShellInfoLibrary::class.java.simpleName

    //write permission
    private val REQUEST_WRITE_EXTERNAL_STORAGE = 1

    //penalty amount which needs to deduct
    private var penaltyAmount = 0.0

    @Inject
    lateinit var masterConfig: ConfigMaster


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

    //mqtt message
    val mqttMessageResponse: MutableLiveData<MqttMessage?> get()= mqttManager.mqttMessageLiveData

    //mqtt connection callback
    val mqttIsConnected: MutableLiveData<Boolean> get()= mqttManager.mqttConnectionLiveData


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

    override fun setActivity(activity: AppCompatActivity) {
        this.activity=activity
    }

    override fun setHttpProtocol(type: HttpType) {

        //set http types
        spUtils.savePreference(SpConstants.API_HTTP_TYPE,type.protocol)

    }


    override fun start(initData: InitData) {

        //Date time library init for backward compatibility
        AndroidThreeTen.init(activity)

        //fetch the dependant initial data
        runBlocking {

            val fetchData= networkCall.getAllData()

            if(fetchData.isSuccess){
               sharedDataManager.sendLibraryInit(true)
            }else{
                sharedDataManager.sendLibraryInit(false)
            }
        }

        //start simulation payment app data
        //startSimulation()

        //addDummyPass()

        //save application specific data in shared preferences for future use
//        spUtils.savePreference(SpConstants.APP_ID,initData.appId)
//        spUtils.savePreference(SpConstants.APP_NAME,initData.appName)
//        spUtils.savePreference(SpConstants.APP_VERSION_CODE,initData.appVersionCode)
//        spUtils.savePreference(SpConstants.APP_VERSION_NAME,initData.appVersionName)
//        spUtils.savePreference(SpConstants.APP_TYPE,initData.appType)
        spUtils.savePreference(SpConstants.DEVICE_TYPE, initData.deviceType.name)
        spUtils.savePreference(SpConstants.DEVICE_SERIAL,initData.deviceSerial)


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

        // Initialize Timber for Debug build
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }


        //start ipc service
        activity?.let { startIpcService(it) }

        //handle permissions
        handlePermissions()

        //handle card read service
        handleCardDetectionService()

    }

    override fun stop() {

        //start ipc service
        activity?.let { stopIpcService(it) }

        stopLogging(true,true)

        disconnectMqtt()
    }

    /**
     * Method will observe the ipc service connection success,
     * once success it will check device type and stop active card detection service of payment app
     */
    private fun handleCardDetectionService(){

        ipcDataHandler.isIpcConnected.observe(activity!!, Observer {

            //check device type to handle data request
            val deviceType= spUtils.getPreference(SpConstants.DEVICE_TYPE,"")

            if(deviceType.isNotEmpty()){

                //get enum type from device type
                val equipmentType= EquipmentType.fromEquipment(deviceType)

                when(equipmentType){

                    EquipmentType.TOM,
                    EquipmentType.TVM->{

                        //create base message
                        val baseMessage= BaseMessage(MSG_ID_STOP_CARD_DETECTION,NcmcDataType.NONE,"")

                        //stop continues card detection service in Payment application
                        sendMessageToIpcService(MSG_ID_STOP_CARD_DETECTION,baseMessage)
                    }

                    EquipmentType.TR,
                    EquipmentType.VALIDATOR->{

                        //get the service ids
                        val csaServiceId= spUtils.getPreference(COMMON_SERVICE_ID,0x1010)
                        val osaServiceId= spUtils.getPreference(OPERATOR_SERVICE_ID,0x0012)

                        //create service info object
                        val serviceInfo= ServiceInfo(commonServiceId = csaServiceId, operatorServiceId = osaServiceId)

                        //create base message
                        val baseMessage= BaseMessage(MSG_ID_START_CARD_DETECTION,NcmcDataType.ALL,serviceInfo)

                        //start continues card detection service
                        sendMessageToIpcService(MSG_ID_START_CARD_DETECTION,baseMessage)
                    }

                    else->{}
                }
            }else{

                Timber.e(TAG,"Device Type Not Provided")
            }
        })


    }

    /**
     * Method to handle permissions
     */
    private fun handlePermissions(){

        activity?.let { it ->

            permissionsUtils.permissionsState.observe(it, Observer { state ->
                when (state) {
                    is PermissionsUtils.PermissionsState.Granted -> {

                        //start logging
                        startLogging(true,true)

                        //mqtt connection
                        mqttManager.connect()

                        //start ipc service
                        activity?.let { startIpcService(it) }





                    }
                    is PermissionsUtils.PermissionsState.Denied -> {
                        Toast.makeText(activity,"Permissions denied. The app cannot proceed.",Toast.LENGTH_LONG).show()
                    }
                    is PermissionsUtils.PermissionsState.ShouldShowRationale -> {
                        activity?.let { permissionsUtils.checkPermissions(state.permissions, it) }
                    }
                    is PermissionsUtils.PermissionsState.RequestPermissions -> {
                        activity?.let {
                            ActivityCompat.requestPermissions(
                                it,
                                state.permissions,
                                PermissionsUtils.REQUEST_CODE
                            )
                        }
                    }
                    else ->{

                    }
                }
            })

            // Check permissions on create
            val requiredPermissions = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            activity?.let { permissionsUtils.checkPermissions(requiredPermissions, it) }
        }


    }

    override fun getStations() {
        databaseCall.getAllStations()
    }

    override fun getPassZones() {
        databaseCall.getZoneData()
    }

    override fun getTripLimits() {
        databaseCall.getTripLimits()
    }

    override fun getDailyLimits() {
        databaseCall.getDailyLimits()
    }

    override fun getPassTypes() {
        databaseCall.getPassData()
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

    override fun startLogging(localLogs:Boolean, serverLogs:Boolean) {
        loggerImpl.startLogging(localLogs,serverLogs)
    }

    override fun stopLogging(localLogs:Boolean, serverLogs:Boolean) {
        loggerImpl.stopLogging(serverLogs,serverLogs)
    }

    override fun logData(tag: String, message: String) {
        loggerImpl.logData(tag,message)
    }

    override fun logError(tag: String, error: Throwable) {
        loggerImpl.logError(tag,error)
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

    override fun startIpcService(context: Context) {
        ipcDataHandler.startConnection(context)
    }

    override fun stopIpcService(context: Context) {
        ipcDataHandler.stopIpcService(context)
    }

    override fun sendMessageToIpcService(messageId:Int,baseMessage: BaseMessage<*>) {
        ipcDataHandler.sendMessageToService(messageId,baseMessage)
    }

    override fun readNcmcCardData(dataType:NcmcDataType) {

        //get both service id's i.e. CSA and OSA
        val csaServiceId = spUtils.getPreference(COMMON_SERVICE_ID,0x1010)
        val osaServiceId = spUtils.getPreference(OPERATOR_SERVICE_ID,0x0012)

        //base message to send
        var baseMessage:BaseMessage<*>?=null

        //create base message
        when(dataType){

            NcmcDataType.CSA->{
                baseMessage= BaseMessage(MSG_ID_ONE_TIME_READ_CARD_REQUEST,dataType,csaServiceId)
            }

            NcmcDataType.OSA ->{

                //make osa data read flag to true
                isForOsaRead=true

                baseMessage= BaseMessage(MSG_ID_ONE_TIME_READ_CARD_REQUEST,dataType,osaServiceId)
            }

            else ->{
                Timber.e(TAG,">>>NO CSA OSA SELECTED TO FETCH THE DATA")
                return
            }

        }


        //send message to payment application to read the card data
        sendMessageToIpcService(MSG_ID_ONE_TIME_READ_CARD_REQUEST,baseMessage)

    }

    override fun removePenalty(penaltyAmount:Double) {
        //check if the request came from TOM device
        val deviceType = spUtils.getPreference(SpConstants.DEVICE_TYPE,"")
        if(!deviceType.equals(EquipmentType.TOM.name)){

            Timber.e(TAG, "Only TOM Application can Remove Penalty")

            return
        }

        //make penalty flag to true
        isForPenalty=true

        //set penalty amount to deduct
        ipcDataHandler.setPenalty(penaltyAmount)

        //create base message
        val baseMessage= BaseMessage(MSG_ID_REMOVE_PENALTY,NcmcDataType.CSA,"MSG_ID_REMOVE_PENALTY")

        //send message to payment application to create the OSA service
        sendMessageToIpcService(MSG_ID_REMOVE_PENALTY,baseMessage)
    }

    override fun createOsaService() {

        //check if the request came from TOM device
        val deviceType = spUtils.getPreference(SpConstants.DEVICE_TYPE,"")
        if(!deviceType.equals(EquipmentType.TOM.name)){

            Timber.e(TAG, "Only TOM Application can create the Operator Service Area")

            return
        }

        //get osa service id
        val osaServiceId= spUtils.getPreference(OPERATOR_SERVICE_ID,0x0012)

        //make osa create flag to true
        isForOsaCreate=true

        //create base message
        val baseMessage= BaseMessage(MSG_ID_CREATE_OSA_SERVICE,NcmcDataType.OSA,osaServiceId)

        //send message to payment application to create the OSA service
        sendMessageToIpcService(MSG_ID_CREATE_OSA_SERVICE,baseMessage)
    }

    override fun createPass(request: PassRequest){

        //check if the request came from TOM device
        val deviceType = spUtils.getPreference(SpConstants.DEVICE_TYPE,"")
        if(!deviceType.equals(EquipmentType.TOM.name)){

            Timber.e(TAG, "Only TOM Application can create The Passes")

            return
        }

        //get osa service id
        val osaServiceId= spUtils.getPreference(OPERATOR_SERVICE_ID,0x0012)

        //create base message
        val baseMessage= BaseMessage(MSG_ID_CREATE_PASS,NcmcDataType.OSA,osaServiceId)

        //make pass create flag to true and assign pass request data
        isForPassCreate=true
        passCreateRequest=request


        //send message to payment application to create the pass
        sendMessageToIpcService(MSG_ID_CREATE_PASS,baseMessage)
    }

    override fun deletePasses() {

        //TODO only for test
        isForOsaDelete=true

        //check if the request came from TOM device
        val deviceType = spUtils.getPreference(SpConstants.DEVICE_TYPE,"")
        if(!deviceType.equals(EquipmentType.TOM.name)){

            Timber.e(TAG, "Only TOM Application can create the Operator Service Area")

            return
        }

        //get osa service id
        val osaServiceId= spUtils.getPreference(OPERATOR_SERVICE_ID,0x0012)

        //create base message
        val baseMessage= BaseMessage(MSG_ID_CREATE_PASS,NcmcDataType.OSA,osaServiceId)

        //make pass create flag to true and assign pass request data
        isForPassCreate=true

        //send message to payment application to create the OSA service
        sendMessageToIpcService(MSG_ID_CREATE_PASS,baseMessage)
    }

    override fun updatePassValue() {
        TODO("Not yet implemented")
    }


    fun startSimulation(){

        val dataReceived ="{\"messageId\":0,\"dataType\":\"CSA\",\"data\":{\"B\":{\"57\":\"6083263242000066D26126209840000000000F\",\"82\":\"1900\",\"95\":\"0000000000\",\"9F39\":\"07\",\"9F02\":\"000000000000\",\"9F03\":\"000000000000\",\"9F34\":\"\",\"8A\":\"\",\"5F34\":\"01\",\"9F33\":\"0008C8\",\"9F1A\":\"0356\",\"9B\":\"0000\",\"5F2A\":\"0164\",\"9A\":\"240917\",\"9C\":\"00\",\"9F37\":\"3CE7F7DD\",\"9F21\":\"110304\",\"5A\":\"6083263242000066\",\"5F24\":\"261231\",\"9F5B\":\"\",\"9F15\":\"1234\",\"9F16\":\"313233343536373839414243444546\",\"9F35\":\"96\",\"5F25\":\"180101\",\"8E\":\"000000000000000042035E031F03\",\"9F0D\":\"A468FC9800\",\"9F0E\":\"1010000000\",\"9F0F\":\"A468FC9800\",\"9F1C\":\"0000000000000000\",\"DF33\":\"011010AC00C39E9301000000002AE92409121714531E60000000000092035660000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\"},\"serviceRelatedData\":[0, 121, -1, -127, 2, 115, -1, 3, 112, -33, 22, 2, 16, 16, -33, 84, 5, 8, 16, 16, -111, 0, -33, 23, 96, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 48, 48],\"serviceDataIndex\":25}}"

        val moshi = Moshi.Builder().build()

        // Convert JSON string back to BaseMessage object
        val baseMessage: BaseMessage<*>? = convertFromJson<BF200Data>(dataReceived,moshi)


        ipcDataHandler.handlePaymentAppMessage(baseMessage!!)
    }

    inline fun <reified T> convertFromJson(json: String, moshi: Moshi): BaseMessage<T>? {
        return try {
            val type = Types.newParameterizedType(BaseMessage::class.java, T::class.java)
            val adapter = moshi.adapter<BaseMessage<T>>(type)
            adapter.fromJson(json)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}