package com.shellinfo.common.code

import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import com.shellinfo.common.code.enums.ApiMode
import com.shellinfo.common.code.enums.HttpType
import com.shellinfo.common.code.enums.ModeType
import com.shellinfo.common.code.enums.NcmcDataType
import com.shellinfo.common.code.enums.PrinterType
import com.shellinfo.common.code.printer.PrinterProcessor
import com.shellinfo.common.data.local.data.InitData
import com.shellinfo.common.data.local.data.ipc.base.BaseMessage
import com.shellinfo.common.data.local.data.mqtt.BaseMessageMqtt
import com.shellinfo.common.data.remote.response.model.fare.FareRequest
import com.shellinfo.common.data.remote.response.model.pass.PassRequest
import com.shellinfo.common.data.remote.response.model.payment_gateway.AppPaymentRequest
import com.shellinfo.common.data.remote.response.model.ticket.Ticket
import com.shellinfo.common.data.remote.response.model.ticket.TicketRequest

interface ShellInfoProvider {

    fun setApiMode(mode: ApiMode)

    fun setBaseUrl(baseUrl:String)

    fun setPort(port:String)

    fun seAuthToken(token:String)

    fun setActivity(activity: AppCompatActivity)

    fun start(initData: InitData)

    fun stop()

    fun setHttpProtocol(protocol: HttpType)

    fun getStations()

    fun getPassZones()

    fun getTripLimits()

    fun getDailyLimits()

    fun getPassTypes()

    fun getStationsByCorridorId(id:Int)

    fun getStationsByCorridorName(name:String)

    fun searchStations(keyword:String)

    fun getStationByStationId(id:String)

    fun getFare(fareRequest: FareRequest)

    fun generateTicket(ticketRequest: TicketRequest)

    fun getBarcode(ticket: Ticket, width:Int, height:Int):Bitmap

    fun getServerDateTime()

    fun isTicketBookDateTimeValid(startTime: String, endTime: String):Boolean

    fun doPayment(appPaymentRequest: AppPaymentRequest)

    fun getPrinter(type:PrinterType):PrinterProcessor

    fun startLogging(localLogs:Boolean, serverLogs:Boolean)

    fun stopLogging(localLogs:Boolean, serverLogs:Boolean)

    fun logData(tag:String, message:String)

    fun logError(tag:String, error:Throwable)

    fun mqttConnect()

    fun subscribeMqttTopic(topic:String)

    fun unsubscribeMqttTopic(topic:String)

    fun sendMqttAck(message:BaseMessageMqtt<*>)

    fun disconnectMqtt()

    fun startIpcService(context: Context)

    fun stopIpcService(context: Context)

    fun sendMessageToIpcService(messageId:Int,baseMessage: BaseMessage<*>)

    fun readNcmcCardData(dataType:NcmcDataType)

    fun removePenalty(penaltyAmount:Double)

    fun createOsaService()

    fun createPass(request: PassRequest)

    fun deletePasses()

    fun deleteData(dataType:NcmcDataType)

    fun updatePassValue()

    fun getCurrentMode():ModeType

    fun getCurrentTime():String
}