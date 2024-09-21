package com.shell.library

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.shellinfo.common.code.ShellInfoLibrary
import com.shellinfo.common.code.enums.ApiMode
import com.shellinfo.common.code.enums.EquipmentType
import com.shellinfo.common.data.local.data.InitData
import com.shellinfo.common.data.shared.SharedDataManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var shellInfoLibrary: ShellInfoLibrary

    @Inject
    lateinit var sharedDataManager: SharedDataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        shellInfoLibrary.setApiMode(ApiMode.PUBLIC)
        shellInfoLibrary.setBaseUrl("https://app.tsavaari.com/LTProject/")
        shellInfoLibrary.seAuthToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJDbGllbnRJZCI6IjUwMDA4ODYxMDMzNDQiLCJDbGllbnRTZWNyZXQiOiI2MjdDRkE2OTMzNDU4QzI4MEUwMjc4NTY1REE2OEE5QUExODUyMzI4IiwiR3JhbnRUeXBlIjoicGFzc3dvcmQiLCJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1lIjoidHNhdmFhcmlfbWVyY2hhbnQiLCJVc2VySWQiOiI1MDAwIiwiQ2xpZW50RW1haWwiOiJ0c2F2YWFyaUBnbWFpbC5jb20iLCJDbGllbnROdW1iZXIiOiI5OTk5OTk5OTk5IiwiQXBwbGljYXRpb25JZCI6IjIwMCIsIk93bmVySW5mbyI6IjIwMCIsImp0aSI6ImY2MTIwYWIzLWNhMTEtNDg0Ni04YzM1LTcxZmI5NTYxMmM5MCIsImV4cCI6MTcxMzYzNjYyOCwiaXNzIjoiTCZUIE1ldHJvIFJhaWwgKEh5ZGVyYWJhZCkgTGltaXRlZCIsImF1ZCI6IkwmVCBNZXRybyBSYWlsIChIeWRlcmFiYWQpIExpbWl0ZWQifQ.xiZCR1LniCGKBokbzh7jHMLpK8w0-X_S3uhVcZoQKcE")

        val initData = InitData(BuildConfig.APPLICATION_ID,
            "Transit",BuildConfig.VERSION_CODE.toString(),
            BuildConfig.VERSION_NAME,
            EquipmentType.TR,
            "Transit",
            Build.SERIAL)


        shellInfoLibrary.setActivity(this)
        shellInfoLibrary.start(initData)


        sharedDataManager.cardData.observe(this, Observer { data ->
            // Handle the observed data
            Log.e("Data Got",">>>> Done")
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


    }
}
