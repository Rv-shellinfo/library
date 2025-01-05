package com.shellinfo.common.code

import abbasi.android.filelogger.FileLogger
import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.work.Configuration
import androidx.work.WorkManager
import com.instacart.library.truetime.TrueTime
import com.jakewharton.threetenabp.AndroidThreeTen
import com.shellinfo.common.BuildConfig
import com.shellinfo.common.code.enums.ApiMode
import com.shellinfo.common.code.enums.EquipmentType
import com.shellinfo.common.code.enums.HttpType
import com.shellinfo.common.code.enums.ModeType
import com.shellinfo.common.code.enums.NcmcDataType
import com.shellinfo.common.code.enums.PrinterType
import com.shellinfo.common.code.ipc.IPCDataHandler
import com.shellinfo.common.code.logs.LoggerImpl
import com.shellinfo.common.code.mqtt.MQTTManager
import com.shellinfo.common.code.mqtt.topic_handler.modes.ModeManager
import com.shellinfo.common.code.printer.PrinterActions
import com.shellinfo.common.code.printer.PrinterProcessor
import com.shellinfo.common.code.printer.SunmiPrinter
import com.shellinfo.common.code.worker.CustomWorkerFactory
import com.shellinfo.common.data.local.data.InitData
import com.shellinfo.common.data.local.data.ipc.BF200Data
import com.shellinfo.common.data.local.data.ipc.ServiceInfo
import com.shellinfo.common.data.local.data.ipc.base.BaseMessage
import com.shellinfo.common.data.local.data.mqtt.BaseMessageMqtt
import com.shellinfo.common.data.local.db.model.CountAndSumResult
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import com.shellinfo.common.data.remote.response.ApiResponse
import com.shellinfo.common.data.remote.response.model.fare.FareRequest
import com.shellinfo.common.data.remote.response.model.pass.PassRequest
import com.shellinfo.common.data.remote.response.model.payment_gateway.AppPaymentRequest
import com.shellinfo.common.data.remote.response.model.payment_gateway.AppPaymentResponse
import com.shellinfo.common.data.remote.response.model.server.ServerDateTimeResponse
import com.shellinfo.common.data.remote.response.model.ticket.Ticket
import com.shellinfo.common.data.remote.response.model.ticket.TicketRequest
import com.shellinfo.common.data.shared.SharedDataManager
import com.shellinfo.common.utils.BarcodeUtils
import com.shellinfo.common.utils.DateUtils
import com.shellinfo.common.utils.IPCConstants.MSG_ID_CREATE_OSA_SERVICE
import com.shellinfo.common.utils.IPCConstants.MSG_ID_CREATE_PASS
import com.shellinfo.common.utils.IPCConstants.MSG_ID_DELETE_CSA_DATA
import com.shellinfo.common.utils.IPCConstants.MSG_ID_DELETE_OSA_DATA
import com.shellinfo.common.utils.IPCConstants.MSG_ID_ONE_TIME_READ_CARD_REQUEST
import com.shellinfo.common.utils.IPCConstants.MSG_ID_REMOVE_PENALTY
import com.shellinfo.common.utils.IPCConstants.MSG_ID_START_CARD_DETECTION
import com.shellinfo.common.utils.IPCConstants.MSG_ID_STOP_CARD_DETECTION
import com.shellinfo.common.utils.PermissionsUtils
import com.shellinfo.common.utils.SpConstants
import com.shellinfo.common.utils.SpConstants.COMMON_SERVICE_ID
import com.shellinfo.common.utils.SpConstants.DEVICE_TYPE
import com.shellinfo.common.utils.SpConstants.ENTRY_SIDE
import com.shellinfo.common.utils.SpConstants.OPERATOR_SERVICE_ID
import com.shellinfo.common.utils.SpConstants.READER_LOCATION
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import org.eclipse.paho.client.mqttv3.MqttMessage
import timber.log.Timber
import java.util.Date
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
    private val sharedDataManager: SharedDataManager,
    private val modeManager: ModeManager,
    private val workerFactory: CustomWorkerFactory,

) :ShellInfoProvider {

    companion object{
        var isForPenalty=false
        var isForPassCreate=false
        var isForOsaRead=false
        var isForOsaCreate=false
        var isForOsaDelete=false
        var isForDataDelete=false
        var isOsaTrxAbort =false
        var isOsaTrxAbortWithPenalty =false
        lateinit var passCreateRequest: PassRequest
        lateinit var globalActivityContext:AppCompatActivity
    }

    private lateinit var permissionLauncher: ActivityResultLauncher<Intent>

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
        globalActivityContext= activity
    }

    override fun setHttpProtocol(type: HttpType) {

        //set http types
        spUtils.savePreference(SpConstants.API_HTTP_TYPE,type.protocol)

    }


    override fun start(initData: InitData) {

        //start logging
        startLogging(true, true)

        //Date time library init for backward compatibility
        AndroidThreeTen.init(activity)

        //Ntp time
        initializeTrueTime()

        //fetch the dependant initial data
        runBlocking {

            val fetchData= networkCall.getAllData()

            if(fetchData.isSuccess){

                //send library init success
                sharedDataManager.sendLibraryInit(true)

                //observe for mode changes scenarios
                observeForModeChange()

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
        Timber.plant(Timber.DebugTree())


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


    private fun initializeTrueTime() {
        Thread {
            try {
                // Initialize with default NTP pool
                TrueTime.build().initialize()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
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
                        val osaServiceId= spUtils.getPreference(OPERATOR_SERVICE_ID,0x1234)

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

                FileLogger.e(TAG,"Device Type Not Provided")
            }
        })


    }

    /**
     * Method to handle permissions
     */
    private fun handlePermissions(){

        activity?.let { context ->
             permissionLauncher = context.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {

                        mqttManager.connect()
                        context.let { startIpcService(it) }
                    } else {
                        Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        activity?.let { it ->

            permissionsUtils.permissionsState.observe(it, Observer { state ->
                when (state) {
                    is PermissionsUtils.PermissionsState.Granted -> {

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

//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                            val isGranted = Environment.isExternalStorageManager()
//
//                            if(isGranted){
//                                //start logging
    //                                startLogging(true,true)
//
//                                //mqtt connection
//                                mqttManager.connect()
//
//                                //start ipc service
//                                activity?.let { startIpcService(it) }
//
//                                return@Observer
//                            }
//                        }
//
//
//
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                            if (!Environment.isExternalStorageManager()) {
//                                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
//                                intent.data = Uri.parse("package:${context.packageName}")
//                                startActivity(activity!!,intent,null)
//                            }
//                        }else{
//                            activity?.let {
//                                ActivityCompat.requestPermissions(
//                                    it,
//                                    state.permissions,
//                                    PermissionsUtils.REQUEST_CODE
//                                )
//                            }
//                        }
                    }
                    else ->{

                    }
                }
            })


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.parse("package:${context.packageName}")
                    permissionLauncher.launch(intent)
                }else{

                    startLogging(true,true)

                    //mqtt connection
                    mqttManager.connect()

                    //start ipc service
                    activity?.let { startIpcService(it) }
                }
            }else{
                // Check permissions on create
                val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO
                    )
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                } else {
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                }

                activity?.let { permissionsUtils.checkPermissions(requiredPermissions, it) }
            }

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
        databaseCall.getStationByStationIdNew(id)
    }

    override fun getFare(fareRequest: FareRequest) {
        networkCall.fetchFare(fareRequest, ApiMode.PRIVATE)
    }

    override fun generateTicket(ticketRequest: TicketRequest) {
        networkCall.bookTicket(ticketRequest,ApiMode.PRIVATE)
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun mqttConnect() {
        mqttManager.connect()
    }

    override fun subscribeMqttTopic(topic: String) {
        mqttManager.subscribe(topic)
    }

    override fun unsubscribeMqttTopic(topic: String) {
        mqttManager.unsubscribe(topic)
    }

    override fun sendMqttAck(message: BaseMessageMqtt<*>) {
        mqttManager.sendMqttAck(message)
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
        val osaServiceId = spUtils.getPreference(OPERATOR_SERVICE_ID,0x1234)

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
                FileLogger.e(TAG,">>>NO CSA OSA SELECTED TO FETCH THE DATA")
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

            FileLogger.e(TAG, "Only TOM Application can Remove Penalty")

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

            FileLogger.e(TAG, "Only TOM Application can create the Operator Service Area")

            return
        }

        //get osa service id
        val osaServiceId= spUtils.getPreference(OPERATOR_SERVICE_ID,0x1234)

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

            FileLogger.e(TAG, "Only TOM Application can create The Passes")

            return
        }

        //get osa service id
        val osaServiceId= spUtils.getPreference(OPERATOR_SERVICE_ID,0x1234)

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

            FileLogger.e(TAG, "Only TOM Application can create the Operator Service Area")

            return
        }

        //get osa service id
        val osaServiceId= spUtils.getPreference(OPERATOR_SERVICE_ID,0x1234)

        //create base message
        val baseMessage= BaseMessage(MSG_ID_CREATE_PASS,NcmcDataType.OSA,osaServiceId)

        //make pass create flag to true and assign pass request data
        isForPassCreate=true

        //send message to payment application to create the OSA service
        sendMessageToIpcService(MSG_ID_CREATE_PASS,baseMessage)
    }

    override fun deleteData(dataType: NcmcDataType) {
        if(dataType == NcmcDataType.OSA){

            isForDataDelete =true

            //get osa service id
            val osaServiceId= spUtils.getPreference(OPERATOR_SERVICE_ID,0x1234)

            //create base message
            val baseMessage= BaseMessage(MSG_ID_DELETE_OSA_DATA,NcmcDataType.OSA,osaServiceId)

            //send message to payment application to create the OSA service
            sendMessageToIpcService(MSG_ID_DELETE_OSA_DATA,baseMessage)
        }else{

            isForDataDelete =true

            //get osa service id
            val commonService= spUtils.getPreference(COMMON_SERVICE_ID,0x1010)

            //create base message
            val baseMessage= BaseMessage(MSG_ID_DELETE_CSA_DATA,NcmcDataType.CSA,commonService)

            //send message to payment application to create the OSA service
            sendMessageToIpcService(MSG_ID_DELETE_CSA_DATA,baseMessage)
        }
    }

    override fun updatePassValue() {
        TODO("Not yet implemented")
    }

    override fun getCurrentMode(): ModeType {
        return modeManager.getCurrentMode()!!
    }

    override fun getCurrentTime(): String {
        if(TrueTime.isInitialized()){
            return TrueTime.now().toString()
        }else{
            return ""
        }
    }

    override fun setReaderLocation(side: String) {
        spUtils.savePreference(READER_LOCATION,side)
    }

    override fun getCountAndSumForCondition(
        shiftId:String,
        paymentModes: List<Int>,
        transactionTypeId: Int
    ): CountAndSumResult {
        return databaseCall.getTicketCountAndSum(shiftId,paymentModes,transactionTypeId)
    }


    private fun observeForModeChange(){

        modeManager.currentModeLiveData.observe(activity!!) {

            when (it) {
                ModeType.MAINTENANCE_MODE,
                ModeType.OUT_OF_SERVICE_MODE,
                ModeType.EMERGENCY_MODE -> {
                    //create base message
                    val baseMessage= BaseMessage(MSG_ID_STOP_CARD_DETECTION,NcmcDataType.NONE,"")

                    //stop continues card detection service in Payment application
                    sendMessageToIpcService(MSG_ID_STOP_CARD_DETECTION,baseMessage)
                }
                ModeType.IN_SERVICE_MODE -> {

                    if(spUtils.getPreference(DEVICE_TYPE,"") == EquipmentType.VALIDATOR.name){

                        //get the service ids
                        val csaServiceId= spUtils.getPreference(COMMON_SERVICE_ID,0x1010)
                        val osaServiceId= spUtils.getPreference(OPERATOR_SERVICE_ID,0x1234)

                        //create service info object
                        val serviceInfo= ServiceInfo(commonServiceId = csaServiceId, operatorServiceId = osaServiceId)

                        //create base message
                        val baseMessage= BaseMessage(MSG_ID_START_CARD_DETECTION,NcmcDataType.ALL,serviceInfo)

                        //stop continues card detection service in Payment application
                        sendMessageToIpcService(MSG_ID_STOP_CARD_DETECTION,baseMessage)
                    }
                }
                ModeType.POWER_SAVING_MODE -> {}
                ModeType.STATION_CLOSE_MODE -> {

                    /**
                     * Station closed mode if reader side is entry then card detection off
                     */

                    if(spUtils.getPreference(DEVICE_TYPE,"") == EquipmentType.VALIDATOR.name){

                        if(spUtils.getPreference(READER_LOCATION,"ENTRY") == ENTRY_SIDE){
                            //create base message
                            val baseMessage= BaseMessage(MSG_ID_STOP_CARD_DETECTION,NcmcDataType.NONE,"")

                            //stop continues card detection service in Payment application
                            sendMessageToIpcService(MSG_ID_STOP_CARD_DETECTION,baseMessage)
                        }

                    }else{

                        /**
                         * Station closed mode if tvm, tom , tr or ptd then card detection disable
                         */

                        //create base message
                        val baseMessage= BaseMessage(MSG_ID_STOP_CARD_DETECTION,NcmcDataType.NONE,"")

                        //stop continues card detection service in Payment application
                        sendMessageToIpcService(MSG_ID_STOP_CARD_DETECTION,baseMessage)
                    }

                }
                ModeType.DEVICE_CLOSE_MODE -> {}
                ModeType.FARE_BYPASS_TWO_MODE,
                ModeType.FARE_BYPASS_ONE_MODE -> {

                    if(spUtils.getPreference(DEVICE_TYPE,"") == EquipmentType.VALIDATOR.name){

                        if(spUtils.getPreference(READER_LOCATION,"ENTRY") == ENTRY_SIDE){
                            //create base message
                            val baseMessage= BaseMessage(MSG_ID_STOP_CARD_DETECTION,NcmcDataType.NONE,"")

                            //stop continues card detection service in Payment application
                            sendMessageToIpcService(MSG_ID_STOP_CARD_DETECTION,baseMessage)
                        }

                    }
                }
                ModeType.TEST_MODE -> {}
                ModeType.FAILURE_MODE -> {}
                else -> {}
            }
        }
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