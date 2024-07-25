package com.shellinfo.common.code.mqtt

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shellinfo.common.code.ConfigMaster
import com.shellinfo.common.code.enums.MqttTopicType
import com.shellinfo.common.data.local.data.mqtt.BaseMessageMqtt
import com.shellinfo.common.data.local.db.entity.StationsTable
import com.squareup.moshi.JsonAdapter
import dagger.hilt.android.qualifiers.ApplicationContext
import info.mqtt.android.service.Ack
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MQTTManager @Inject constructor(
    private val configMaster: ConfigMaster,
    private val context: Context,
    private val mqttMessageHandler: MqttMessageHandler
){

    @Inject
    lateinit var mqttMessageAdapter: JsonAdapter<BaseMessageMqtt<*>>

    private lateinit var mqttClient: MqttAndroidClient

    private val _mqttMessageLiveData = MutableLiveData<MqttMessage?>()
    val mqttMessageLiveData: MutableLiveData<MqttMessage?> get() = _mqttMessageLiveData

    // TAG
    companion object {
        const val TAG = "AndroidMqttClient"
    }

    fun connect() {
        val serverURI = "tcp://68.233.98.228:1883"
        mqttClient = MqttAndroidClient(context, serverURI, "kotlin_client", Ack.AUTO_ACK)
        mqttClient.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String?, message: MqttMessage?) {

                //parse the mqtt message
                val mqttMessage = mqttMessageAdapter.fromJson(message.toString())

                //handle the mqtt message received from mqtt broker
                mqttMessageHandler.consumeMessage(MqttTopicType.fromTopic(topic),mqttMessage)



            }

            override fun connectionLost(cause: Throwable?) {
                Log.d(TAG, "Connection lost ${cause.toString()}")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {

            }
        })
        val options = MqttConnectOptions()
        try {
            mqttClient.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Connection success")
                    subscribe("APP_UPDATE")


                    val jsonString = """
{
  "message_id":"OTA_UPDATE_TOM",
  "equipmentGroupId":"2001",
  "equipmentGroupName":"TOM",
  "lineId":1,
  "stationId":125,
  "isAllEquipments":false,
  "equipment_id":["123","234","456","678"],
  "data" :{
    "file_name":"release.apk",
    "version":"1.1",
    "ftp_path":"/tom_update/",
    "md5FileHash":"",
    "activationDateTime":"yyyy-mm-dd hh24:mm:ss"
  }
}
""".trimIndent()

                    publish("MQTT_OTA_UPDATE",jsonString)
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Connection failure")
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun subscribe(topic: String, qos: Int = 1) {
        try {
            mqttClient.subscribe(topic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Subscribed to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to subscribe $topic")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun unsubscribe(topic: String) {
        try {
            mqttClient.unsubscribe(topic, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Unsubscribed to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to unsubscribe $topic")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }


    fun publish(topic: String, msg: String, qos: Int = 1, retained: Boolean = false) {
        try {
            val message = MqttMessage()
            message.payload = msg.toByteArray()
            message.qos = qos
            message.isRetained = retained
            mqttClient.publish(topic, message, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "$msg published to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to publish $msg to $topic")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        try {
            mqttClient.disconnect(null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Disconnected")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to disconnect")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

}