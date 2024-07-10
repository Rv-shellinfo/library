package com.shellinfo.common.code

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import com.shellinfo.common.code.enums.ApiMode
import com.shellinfo.common.code.enums.HttpType
import com.shellinfo.common.code.enums.PrinterType
import com.shellinfo.common.code.printer.PrinterProcessor
import com.shellinfo.common.data.remote.response.model.fare.FareRequest
import com.shellinfo.common.data.remote.response.model.payment_gateway.AppPaymentRequest
import com.shellinfo.common.data.remote.response.model.ticket.Ticket
import com.shellinfo.common.data.remote.response.model.ticket.TicketRequest

interface ShellInfoProvider {

    fun setApiMode(mode: ApiMode)

    fun setBaseUrl(baseUrl:String)

    fun setPort(port:String)

    fun seAuthToken(token:String)

    fun setActivity(activity: Activity)

    fun init()

    fun setHttpProtocol(protocol: HttpType)

    fun setStations()

    fun getStations()

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
}