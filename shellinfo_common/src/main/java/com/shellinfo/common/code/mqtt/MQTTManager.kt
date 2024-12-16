package com.shellinfo.common.code.mqtt

import abbasi.android.filelogger.FileLogger
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.shellinfo.common.code.ConfigMaster
import com.shellinfo.common.code.enums.MqttAckTopicType
import com.shellinfo.common.code.enums.MqttTopicType
import com.shellinfo.common.data.local.data.mqtt.BaseMessageMqtt
import com.shellinfo.common.data.local.data.mqtt.MqttData
import com.shellinfo.common.data.local.prefs.SharedPreferenceUtil
import com.shellinfo.common.utils.DateUtils
import com.squareup.moshi.JsonAdapter
import info.mqtt.android.service.Ack
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MQTTManager @Inject constructor(
    private val configMaster: ConfigMaster,
    private val context: Context,
    private val mqttMessageHandler: MqttMessageHandler,
    private val mqttMessageAdapter: JsonAdapter<BaseMessageMqtt<MqttData>>,
    private val sharedPreferenceUtil: SharedPreferenceUtil
){


    private val TAG = MQTTManager::class.java.simpleName

    //mqtt client
    private lateinit var mqttClient: MqttAndroidClient

    //mqtt message live data
    private val _mqttMessageLiveData = MutableLiveData<MqttMessage?>()
    val mqttMessageLiveData: MutableLiveData<MqttMessage?> get() = _mqttMessageLiveData

    //mqtt connection live data
    private val _mqttConnectionLiveData = MutableLiveData<Boolean>()
    val mqttConnectionLiveData: MutableLiveData<Boolean> get() = _mqttConnectionLiveData


    /**
     * Method to connect to MQTT
     */
    fun connect() {

        val serverURI = "tcp://68.233.98.228:1883"
        mqttClient = MqttAndroidClient(context, serverURI, "kotlin_client", Ack.AUTO_ACK)
        mqttClient.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String?, message: MqttMessage?) {

                //log for mqtt connection success
                FileLogger.d(TAG,"MQTT MESSAGE ARRIVED FOR TOPIC -> $topic")

                try {

                    //parse the mqtt message
                    val mqttMessage = mqttMessageAdapter.fromJson(message.toString())

                    //handle the mqtt message received from mqtt broker
                    mqttMessageHandler.consumeMessage(MqttTopicType.fromTopic(topic),mqttMessage)


                }catch (ex:Exception){

                    FileLogger.e(TAG,"ERROR IN CONNECTION ${ex.message}")
                    ex.cause?.let { FileLogger.e(TAG, it) }
                }

            }

            override fun connectionLost(cause: Throwable?) {
                FileLogger.e(TAG, "MQTT Connection lost ${cause.toString()}")
                cause?.let { FileLogger.e(TAG, it) }

            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {

            }
        })

        //MQTT options
        val options = MqttConnectOptions().apply {
            isAutomaticReconnect = true // Enable automatic reconnection
            isCleanSession = false      // Keep the session alive
        }

        try {
            mqttClient.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {


                    FileLogger.d(TAG, "MQTT Connection success")

                    _mqttConnectionLiveData.value= true

                    //set mqtt manager
                    mqttMessageHandler.setMqttManager(this@MQTTManager)

                    //method to subscribe to topics
                    subscribeToTopics()
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {

                    FileLogger.e(TAG, "MQTT Connection failure ${exception?.message}")
                    exception?.let { FileLogger.e(TAG, it) }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            e.let { FileLogger.e(TAG, it) }
        }

    }

    /**
     * Method to subscribe the topic
     */
    fun subscribe(topic: String, qos: Int = 1) {
        try {
            mqttClient.subscribe(topic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {

                    FileLogger.d(TAG, "Subscribed to $topic")


                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    FileLogger.e(TAG, "Failed to subscribe $topic")
                    FileLogger.e(TAG, "Topic Subscription failure ${exception?.message}")
                    exception?.let { FileLogger.e(TAG, it) }
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
            e.let { FileLogger.e(TAG, it) }
        }
    }

    /**
     * Method to unsubscribe a message
     */
    fun unsubscribe(topic: String) {
        try {
            mqttClient.unsubscribe(topic, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {

                    FileLogger.d(TAG, "Unsubscribed to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {

                    FileLogger.e(TAG, "Failed to un-subscribe $topic")
                    FileLogger.e(TAG, "Topic Un-Subscription failure ${exception?.message}")
                    exception?.let { FileLogger.e(TAG, it) }
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
            e.let { FileLogger.e(TAG, it) }
        }
    }


    /**
     * Method to publish a message on a topic
     */
    private fun publish(topic: String, msg: String, qos: Int = 1, retained: Boolean = false) {
        try {
            val message = MqttMessage()
            message.payload = msg.toByteArray()
            message.qos = qos
            message.isRetained = retained
            mqttClient.publish(topic, message, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    FileLogger.d(TAG, "$msg published to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {

                    FileLogger.e(TAG, "Failed to publish $msg to $topic")
                    FileLogger.e(TAG, "Error message ${exception?.message}")
                    exception?.let { FileLogger.e(TAG, it) }
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
            e.let { FileLogger.e(TAG, it) }
        }
    }

    /**
     * Method to disconnect from MQTT server
     */
    fun disconnect() {
        try {
            mqttClient.disconnect(null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    FileLogger.d(TAG, "MQTT Disconnected")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    FileLogger.e(TAG, "Failed to disconnect from MQTT")
                    FileLogger.e(TAG, "MQTT DISCONNECT Error message ${exception?.message}")
                    exception?.let { FileLogger.e(TAG, it) }
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
            e.let { FileLogger.e(TAG, it) }
        }
    }

    /**
     * Method to subscribe the topics
     */
    fun subscribeToTopics(){
        FileLogger.d("Subscribing the topic","Done")

        subscribe(MqttTopicType.OTA_UPDATE.name)
        subscribe(MqttTopicType.LOG_STATUS.name)
        subscribe(MqttTopicType.CONFIG_UPDATE.name)
        subscribe(MqttTopicType.FIRMWARE_UPDATE.name)
        subscribe(MqttTopicType.KEY_INJECTION.name)
        subscribe(MqttTopicType.DEVICE_CONTROL_COMMAND.name)
        subscribe(MqttTopicType.SPECIAL_MODE_COMMAND.name)
        subscribe(MqttTopicType.PARAMETER.name)
        subscribe(MqttTopicType.SLE_DATABASE_STATUS.name)
        subscribe(MqttTopicType.SLE_MESSAGE.name)
    }

    /**
     * Method to send back the acknowledgment
     */
    fun sendMqttAck(message:BaseMessageMqtt<*>){

        //updated base message with current date time
        val updatedMessage= message.copy(activationDateTime = DateUtils.getSysDateTime())

        //convert message to json string
        val jsonString = mqttMessageAdapter.toJson(updatedMessage)

        //publish the message for ack
        publish(MqttAckTopicType.fromAckTopic(updatedMessage.messageId)!!.name,jsonString)

    }

}