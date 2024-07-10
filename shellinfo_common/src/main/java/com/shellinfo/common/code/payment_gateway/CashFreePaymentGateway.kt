package com.shellinfo.common.code.payment_gateway

import android.app.Activity
import android.content.Context
import android.util.Log
import com.cashfree.pg.api.CFPaymentGatewayService
import com.cashfree.pg.cf_analytics.CFAnalyticsService
import com.cashfree.pg.core.api.CFSession
import com.cashfree.pg.core.api.CFTheme
import com.cashfree.pg.core.api.callback.CFCheckoutResponseCallback
import com.cashfree.pg.core.api.utils.CFErrorResponse
import com.cashfree.pg.core.api.utils.ThreadUtil
import com.cashfree.pg.core.hidden.CFPaymentService
import com.cashfree.pg.ui.api.CFDropCheckoutPayment
import com.cashfree.pg.ui.api.CFPaymentComponent
import com.cashfree.pg.ui.hidden.channel.CFNativeCallbackEventBus
import com.cashfree.pg.ui.hidden.channel.CFNativeCallbackEvents
import com.shellinfo.common.BuildConfig
import com.shellinfo.common.code.NetworkCall
import com.shellinfo.common.data.remote.repository.ApiRepository
import com.shellinfo.common.data.remote.response.ApiResponse
import com.shellinfo.common.data.remote.response.model.payment_gateway.AppPaymentRequest
import com.shellinfo.common.data.remote.response.model.payment_gateway.AppPaymentResponse
import com.shellinfo.common.data.remote.response.model.payment_gateway.cash_free.CashFreePaymentRequest
import com.shellinfo.common.data.remote.response.model.payment_gateway.cash_free.CashFreePaymentResponse
import com.shellinfo.common.data.remote.response.model.payment_gateway.cash_free.CustomerDetails
import com.shellinfo.common.data.remote.response.model.payment_gateway.cash_free.OrderMeta
import com.shellinfo.common.data.remote.response.model.payment_gateway.order_status.OrderStatusRequest
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import javax.inject.Inject

class CashFreePaymentGateway @Inject constructor(
    private val apiRepository: ApiRepository,
    private val networkCall: NetworkCall,
    private val moshi: Moshi
) : PaymentGateway {





    lateinit var payDetails: AppPaymentRequest

    lateinit var cfOrderId: String

    private var job: Job? = null

    override fun processPayment(payRequest: AppPaymentRequest, context: Activity) {

        //assign pay details
        payDetails=payRequest

        job?.cancel() // Cancel the previous job if it's still running


        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                //customer detail set
                val cDetails= CustomerDetails(
                    customer_id = payRequest.customer_id,
                    customer_name = payRequest.customer_name,
                    customer_email = payRequest.customer_email,
                    customer_phone = payRequest.customer_mobile
                )

                //order meta for main request
                val orderMeta= OrderMeta(
                    notify_url = BuildConfig.CASH_FREE_NOTIFY_URL
                )



                //create request for cash free api call
                val request = CashFreePaymentRequest(
                    order_amount = payRequest.totalAmount,
                    order_id = payRequest.orderId!!,
                    order_currency = "INR",
                    customer_details = cDetails,
                    order_meta = orderMeta,
                    order_note ="QRTicket Description"
                )


//            //convert request in string json
//            val adapter = moshi.adapter(CashFreePaymentRequest::class.java)
//            val requestString = adapter.toJson(request)

                //call api for cash free
                apiRepository.doCashFreePaymentInit(request).collect{ response->

                    when(response){

                        is ApiResponse.Loading ->{

                        }

                        is ApiResponse.Success ->{


                            context?.runOnUiThread {
                                startCashFreeCheckout(cashFreeResponse = response.data,context)
                            }

                        }

                        is ApiResponse.Error ->{
                            networkCall._paymentGatewayResponse.value=ApiResponse.Error("error", Exception("Init error"))
                        }

                        else ->{
                            networkCall._paymentGatewayResponse.value=ApiResponse.Error("error", Exception("Init error"))
                        }
                    }
                }


            } catch (e: Exception) {
                // Handle error
            }
        }




    }


    fun startCashFreeCheckout(cashFreeResponse:CashFreePaymentResponse, context: Context){


        //set cashfree order id
        this.cfOrderId=cashFreeResponse.cf_order_id

        //set cash free environment
        val environment= CFSession.Environment.SANDBOX

        //init cash free session
        val cfSession = CFSession.CFSessionBuilder()
            .setEnvironment(environment)
            .setPaymentSessionID(cashFreeResponse.payment_session_id)
            .setOrderId(cashFreeResponse.order_id)
            .build();

        //enable payment modes
        val cfPaymentComponent = CFPaymentComponent.CFPaymentComponentBuilder()
            .add(CFPaymentComponent.CFPaymentModes.CARD)
            .add(CFPaymentComponent.CFPaymentModes.UPI)
            .add(CFPaymentComponent.CFPaymentModes.WALLET)
            .add(CFPaymentComponent.CFPaymentModes.NB)
            .build()

        //set theme for payment gateway
        val cfTheme = CFTheme.CFThemeBuilder()
            .setNavigationBarBackgroundColor("#004b87")
            .setNavigationBarTextColor("#ffffff")
            .setButtonBackgroundColor("#004b87")
            .setButtonTextColor("#ffffff")
            .setPrimaryTextColor("#000000")
            .setSecondaryTextColor("#000000")
            .build()

        // checkout builder
        val cfCheckoutBuilder = CFDropCheckoutPayment.CFDropCheckoutPaymentBuilder()
            .setCFNativeCheckoutUITheme(cfTheme)
            .setCFUIPaymentModes(cfPaymentComponent)
            .setSession(cfSession)
            .build()


        val paymentGatewayService : CFPaymentGatewayService = CFPaymentGatewayService.getInstance()


        //callback
        CFNativeCallbackEventBus.initialize(Executors.newSingleThreadExecutor())
        val data= CFNativeCallbackEventBus.getInstance()
        data.subscribe { event ->


            if(event.errorResponse == null ){

                //success transction
                getTickets()

            }else{

                //failed transaction
                networkCall._paymentGatewayResponse.value = ApiResponse.Error("error", Exception("Payment failed error"))
            }



            CFPaymentService.getInstance().isFromUI = false
            CFPaymentService.getInstance().isSeamlessUI = false
            CFAnalyticsService.getInstance().stopCapturing()
            CFAnalyticsService.getInstance().sendPaymentEventsToBackendWithSessionID()
        }


        //start payment
        paymentGatewayService.doPayment(context,cfCheckoutBuilder)
    }


    /**
     * Get tickets by order id
     */
    fun getTickets(){

        //create ticket request with order id
        val request= OrderStatusRequest(
            merchantOrderId = cfOrderId,
            amount = payDetails.totalAmount,
            mobno = payDetails.customer_mobile
        )

        job?.cancel()

        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                apiRepository.getTicketByOrderId(request).collect{ response->

                    when(response){

                        is ApiResponse.Loading ->{

                        }

                        is ApiResponse.Success ->{

                            val successResponse= AppPaymentResponse(
                                returnCode= response.data.returnCode,
                                returnMsg = response.data.returnMsg,
                                ltmrhlPurchaseId= response.data.ltmrhlPurchaseId,
                                tickets = response.data.tickets
                            )

                            networkCall._paymentGatewayResponse.value=ApiResponse.Success(successResponse)
                        }

                        is ApiResponse.Error ->{
                            networkCall._paymentGatewayResponse.value=ApiResponse.Error("error", Exception("Get tickets by order id fail"))
                        }

                        else ->{
                            networkCall._paymentGatewayResponse.value=ApiResponse.Error("error", Exception("Get tickets by order id fail"))
                        }
                    }

                }
            } catch (e: Exception) {
                // Handle error
            }
        }

    }



}