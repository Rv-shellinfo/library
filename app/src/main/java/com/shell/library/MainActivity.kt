package com.shell.library

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.shellinfo.common.code.ShellInfoLibrary
import com.shellinfo.common.code.enums.ApiMode
import com.shellinfo.common.code.enums.DeviceControlCommandType
import com.shellinfo.common.code.enums.EquipmentType
import com.shellinfo.common.code.enums.NcmcDataType
import com.shellinfo.common.code.enums.SpecialModesCommandType
import com.shellinfo.common.code.enums.TicketType
import com.shellinfo.common.data.local.data.InitData
import com.shellinfo.common.data.local.data.mqtt.BaseMessageMqtt
import com.shellinfo.common.data.local.data.mqtt.DeviceControlMessage
import com.shellinfo.common.data.local.data.mqtt.SleDynamicMessage
import com.shellinfo.common.data.local.data.mqtt.SpecialModeMessage
import com.shellinfo.common.data.remote.response.ApiResponse
import com.shellinfo.common.data.remote.response.model.fare.FareRequest
import com.shellinfo.common.data.remote.response.model.pass.BankTransactionDetail
import com.shellinfo.common.data.remote.response.model.pass.PassRequest
import com.shellinfo.common.data.remote.response.model.ticket.TicketRequest
import com.shellinfo.common.data.shared.SharedDataManager
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var shellInfoLibrary: ShellInfoLibrary

    @Inject
    lateinit var sharedDataManager: SharedDataManager

    lateinit var btnStart:Button;
    lateinit var btnGetOsaData:Button;
    lateinit var btnRemovePenalty:Button;
    lateinit var btnCreateOSAService:Button;
    lateinit var btnCreateHolidayPass:Button;
    lateinit var btnCreateTripPass:Button;
    lateinit var btnCreateZonePass:Button;
    lateinit var btnClearOSA:Button;
    lateinit var btnDelteData:Button;
    lateinit var btnGetFare:Button;
    lateinit var btnGenerateTicket:Button;
    lateinit var btnGetBitmap:Button;


    //1. global mqtt message
    var globalMqttMessage: BaseMessageMqtt<*>? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //set button
        btnStart= findViewById(R.id.btnStart)
        btnRemovePenalty= findViewById(R.id.btnRemovePenalty)
        btnGetOsaData= findViewById(R.id.btnGetOsaData)
        btnCreateOSAService= findViewById(R.id.btnCreateOSAService)
        btnCreateHolidayPass= findViewById(R.id.btnCreateHolidayPass)
        btnCreateTripPass= findViewById(R.id.btnCreateTripPass)
        btnCreateZonePass= findViewById(R.id.btnCreateZonePass)
        btnDelteData= findViewById(R.id.btnDelteData)
        btnClearOSA= findViewById(R.id.btnClearOSA)
        btnGetFare= findViewById(R.id.btnGetFare)
        btnGenerateTicket= findViewById(R.id.btnGenerateTicket)
        btnGetBitmap= findViewById(R.id.btnGetBitmap)

        //shellInfoLibrary.setApiMode(ApiMode.PUBLIC)
        //shellInfoLibrary.setBaseUrl("https://app.tsavaari.com/LTProject/")
        //shellInfoLibrary.seAuthToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJDbGllbnRJZCI6IjUwMDA4ODYxMDMzNDQiLCJDbGllbnRTZWNyZXQiOiI2MjdDRkE2OTMzNDU4QzI4MEUwMjc4NTY1REE2OEE5QUExODUyMzI4IiwiR3JhbnRUeXBlIjoicGFzc3dvcmQiLCJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1lIjoidHNhdmFhcmlfbWVyY2hhbnQiLCJVc2VySWQiOiI1MDAwIiwiQ2xpZW50RW1haWwiOiJ0c2F2YWFyaUBnbWFpbC5jb20iLCJDbGllbnROdW1iZXIiOiI5OTk5OTk5OTk5IiwiQXBwbGljYXRpb25JZCI6IjIwMCIsIk93bmVySW5mbyI6IjIwMCIsImp0aSI6ImY2MTIwYWIzLWNhMTEtNDg0Ni04YzM1LTcxZmI5NTYxMmM5MCIsImV4cCI6MTcxMzYzNjYyOCwiaXNzIjoiTCZUIE1ldHJvIFJhaWwgKEh5ZGVyYWJhZCkgTGltaXRlZCIsImF1ZCI6IkwmVCBNZXRybyBSYWlsIChIeWRlcmFiYWQpIExpbWl0ZWQifQ.xiZCR1LniCGKBokbzh7jHMLpK8w0-X_S3uhVcZoQKcE")

        val initData = InitData(
            appId = BuildConfig.APPLICATION_ID,
            appName = "Transit",BuildConfig.VERSION_CODE.toString(),
            appVersionName = BuildConfig.VERSION_NAME,
            deviceType = EquipmentType.TOM,
            appType = "Transit",
            deviceSerial =  Build.SERIAL,
            apiMode = ApiMode.PUBLIC,
            "")


        shellInfoLibrary.setActivity(this)
        shellInfoLibrary.start(initData)



        sharedDataManager.zoneData.observe(this,Observer { data ->
            // Handle the observed data
            Log.e("Data Got",">>>> Done")
        })



        sharedDataManager.isLibraryInit.observe(this) { data ->

            data.getContentIfNotHandled()?.let {
                // Handle the observed data
                Log.e("Data Got", ">>>> Done")

                //log data
                shellInfoLibrary.logData("MY TAG", "Testing the Tag")
            }

        }


        shellInfoLibrary.getStations()

        sharedDataManager.stationData.observe(this, Observer { data ->
            // Handle the observed data
            Log.e("Data Got",">>>> Done")
        })


        sharedDataManager.csaData.observe(this, Observer { data ->
            // Handle the observed data
            Log.e("Data Got",">>>> Done")
        })

        sharedDataManager.osaData.observe(this, Observer { data ->
            // Handle the observed data
            Log.e("Data OSA Got",">>>> Done")

            val osaData= data
        })


        sharedDataManager.sleMessage.observe(this){event->

            event.getContentIfNotHandled()?.let { message ->
                // 3. assign incoming message to global message type
                globalMqttMessage= message

                // 4. cast your message to Device Control Message type
                val sleMessage = message.data as SleDynamicMessage
            }
        }

        //this for logging
        shellInfoLibrary.logData("TAG","Message")

        try{

        }catch (ex:Exception){

            //this is for error
            shellInfoLibrary.logError("TAG",ex)
        }


        //2. observable for device control commands
        sharedDataManager.deviceControlCommand.observe(this) { event->

                event.getContentIfNotHandled()?.let { message ->
                    // 3. assign incoming message to global message type
                    globalMqttMessage= message

                    // 4. cast your message to Device Control Message type
                    val deviceControlMessage = message.data as DeviceControlMessage


                    // 5. check for command type
                    when(DeviceControlCommandType.getDeviceControlCommand(deviceControlMessage.commandTypeId) ){
                        DeviceControlCommandType.IN_SERVICE_MODE ->{
                            // 6. TODO do your code here based on message type
                        }
                        DeviceControlCommandType.OUT_OF_SERVICE_MODE ->{

                            // 6. TODO do your code here based on message type

                        }

                        DeviceControlCommandType.MAINTENANCE_MODE ->{

                        }

                        DeviceControlCommandType.POWER_SAVING_MODE ->{

                        }

                        DeviceControlCommandType.TEST_MODE ->{

                        }

                        else ->{}
                    }
                }
        }



        // 7. After your code execution acknowledge to server
        //shellInfoLibrary.sendMqttAck(globalMqttMessage!!)


        //2. observable for device control commands
        sharedDataManager.specialModeCommand.observe(this) { event->

            event.getContentIfNotHandled()?.let { message ->
                // 3. assign incoming message to global message type
                globalMqttMessage= message

                // 4. cast your message to Device Control Message type
                val specialModeMessage = message.data as SpecialModeMessage


                // 5. check for command type
                when(SpecialModesCommandType.getSpecialModeCommand(specialModeMessage.commandTypeId) ){

                    SpecialModesCommandType.EMERGENCY_MODE->{
                        // 6. TODO do your code here based on message type
                    }

                    SpecialModesCommandType.INCIDENT_MODE->{
                        // 6. TODO do your code here based on message type
                    }

                    SpecialModesCommandType.STATION_CLOSE_MODE->{

                    }

                    SpecialModesCommandType.DEVICE_CLOSE_MODE->{

                    }


                    else ->{}
                }

            }
        }

        // 7. After your code execution acknowledge to server
        //shellInfoLibrary.sendMqttAck(globalMqttMessage!!)

        btnStart.setOnClickListener(View.OnClickListener {

            //read card passive mode
            shellInfoLibrary.readNcmcCardData(NcmcDataType.CSA)
        })


        btnRemovePenalty.setOnClickListener(View.OnClickListener {

            //read card passive mode
            shellInfoLibrary.removePenalty(100.0)
        })


        btnCreateOSAService.setOnClickListener(View.OnClickListener {

            shellInfoLibrary.createOsaService()
        })

        btnGetOsaData.setOnClickListener(View.OnClickListener {

            //read card passive mode
            shellInfoLibrary.readNcmcCardData(NcmcDataType.OSA)
        })

        btnCreateHolidayPass.setOnClickListener(View.OnClickListener {
            val bankDetail = BankTransactionDetail(paymentMethodId =100)
            val passRequest = PassRequest(
                productType = 100,
                isZonePass = true,
                zoneId=1,
                zoneAmount = 10.0,
                passLimitId=1,
                passLimitValue = 30,
                dailyLimitId=2,
                dailyLimitValue = 2,
                bankDetail = bankDetail,
                productCode = "HOLIDAY_PASS",
                amount = 100.0)
            shellInfoLibrary.createPass(passRequest)
        })

        btnCreateTripPass.setOnClickListener(View.OnClickListener {

            val bankDetail = BankTransactionDetail(paymentMethodId =100)
            val passRequest = PassRequest(
                productType = 103,
                isZonePass = false,
                passLimitId=3,
                passLimitValue = 50,
                dailyLimitId=5,
                dailyLimitValue = 10,
                sourceStationId="0401",
                destStationId = "0402",
                bankDetail = bankDetail,
                productCode = "WEEKLY_PASS",
                amount = 100.0)
            shellInfoLibrary.createPass(passRequest)
        })

        btnCreateZonePass.setOnClickListener(View.OnClickListener {
            val bankDetail = BankTransactionDetail(paymentMethodId =100,)
            val passRequest = PassRequest(105, passLimitValue = 30, zoneId = 1, zoneAmount = 10.0, bankDetail = bankDetail,productCode = "MONTHLY", amount = 300.0)
            shellInfoLibrary.createPass(passRequest)
        })


        btnClearOSA.setOnClickListener(View.OnClickListener {
            shellInfoLibrary.deletePasses()
        })

        btnDelteData.setOnClickListener(View.OnClickListener {
            shellInfoLibrary.deleteData(NcmcDataType.OSA)
        })




        btnGetFare.setOnClickListener(View.OnClickListener {
            val fareRequest = FareRequest()
            fareRequest.fromStationId="0301"
            fareRequest.toStationId="0302"
            fareRequest.ticketTypeId =TicketType.SJT.type
            fareRequest.merchantId ="2000886106344"

            shellInfoLibrary.getFare(fareRequest)

            sharedDataManager.fareData.observe(this) { event->

                event.getContentIfNotHandled()?.let {
                    when (it) {
                        is ApiResponse.Loading -> {}
                        is ApiResponse.Success -> {
                            Log.e("Success","Success")
                        }
                        is ApiResponse.Error -> {
                            Log.e("Error","Error")
                        }
                        else -> {}
                    }
                }


            }
        })

        btnGenerateTicket.setOnClickListener(View.OnClickListener {
            val request = TicketRequest(
                merchantOrderId = "123456",
                merchantId = "2000886106344",
                fromStationId = "0301",
                toStationid = "0302",
                ticketTypeId = TicketType.SJT.type,
                noOfTickets = "1",
                ltmrhlPassId="",
                patronPhoneNumber=""
            )


            shellInfoLibrary.generateTicket(request)

            sharedDataManager.ticketData.observe(this) { event->

                event.getContentIfNotHandled()?.let {
                    when (it) {
                        is ApiResponse.Loading -> {}
                        is ApiResponse.Success -> {
                            Log.e("Success","Success")
                        }
                        is ApiResponse.Error -> {
                            Log.e("Error","Error")
                        }
                        else -> {}
                    }
                }


            }
        })

        btnGetBitmap.setOnClickListener(View.OnClickListener {

            //val bitmap = shellInfoLibrary.getBarcode()
        })



        //shellInfoLibrary.mqttConnect()
        //shellInfoLibrary.subscribeMqttTopic("APP_UPDATE")
//        shellInfoLibrary.mqttMessageResponse.observe(this){message->
//
//           Log.e("message","message")
//        }

        //shellInfoLibrary.publishMqttMessage("MQTT_OTA_UPDATE","eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")
       // shellInfoLibrary.initKafka()
        //shellInfoLibrary.startKafka()

        //shellInfoLibrary.sendKafkaMessage("ag_trx_topic","trx_type","QR")

    }

    override fun onDestroy() {
        super.onDestroy()

        shellInfoLibrary.disconnectMqtt()


    }
}
