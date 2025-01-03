package com.shellinfo.common.utils

import com.shellinfo.common.utils.IPCConstants.AMT_NOT_SUFFICIENT
import com.shellinfo.common.utils.IPCConstants.APPLICATION_BLOCKED
import com.shellinfo.common.utils.IPCConstants.BIN_PROHIBITED
import com.shellinfo.common.utils.IPCConstants.CARD_ALREADY_TAPPED
import com.shellinfo.common.utils.IPCConstants.CARD_BLACKLISTED
import com.shellinfo.common.utils.IPCConstants.CARD_EXPIRED
import com.shellinfo.common.utils.IPCConstants.CSA_NOT_PRESENT
import com.shellinfo.common.utils.IPCConstants.CSA_PRESENT_ALL_PASS_INVALID
import com.shellinfo.common.utils.IPCConstants.ENTRY_NOT_FOUND
import com.shellinfo.common.utils.IPCConstants.EXIT_NOT_FOUND
import com.shellinfo.common.utils.IPCConstants.FAILURE_ENTRY_VALIDATION
import com.shellinfo.common.utils.IPCConstants.FAILURE_FARE_API
import com.shellinfo.common.utils.IPCConstants.FAILURE_FARE_CALC
import com.shellinfo.common.utils.IPCConstants.NO_ERROR
import com.shellinfo.common.utils.IPCConstants.OPERATOR_ID_MISMATCH
import com.shellinfo.common.utils.IPCConstants.READER_FUNCTIONALITY_DISABLED
import com.shellinfo.common.utils.IPCConstants.STYL_CARD_READ_ERROR
import com.shellinfo.common.utils.IPCConstants.STYL_COMMAND_EXE_FAILED
import com.shellinfo.common.utils.IPCConstants.STYL_EXPIRED_CARD
import com.shellinfo.common.utils.IPCConstants.STYL_INVALID_COMMAND_PARAM
import com.shellinfo.common.utils.IPCConstants.STYL_NOT_ACCEPTED_OUTCOME
import com.shellinfo.common.utils.IPCConstants.STYL_NO_CARD_DETECTED
import com.shellinfo.common.utils.IPCConstants.STYL_NO_ERROR
import com.shellinfo.common.utils.IPCConstants.STYL_NO_RESPONSE
import com.shellinfo.common.utils.IPCConstants.STYL_NO_USB_PERMISSION
import com.shellinfo.common.utils.IPCConstants.STYL_ODA_ERROR_1
import com.shellinfo.common.utils.IPCConstants.STYL_ODA_ERROR_2
import com.shellinfo.common.utils.IPCConstants.STYL_READER_BUSY
import com.shellinfo.common.utils.IPCConstants.TIME_EXCEEDED
import com.shellinfo.common.utils.IPCConstants.TORN_TRANSACTION

object MessageUtils {

    fun getMessage(messageId:Int):String{

        var message = ""

        when(messageId){
            STYL_NO_ERROR->{
                message = "NO ERROR"
            }
            STYL_COMMAND_EXE_FAILED->{
                message = "Command execution failed"
            }
            STYL_INVALID_COMMAND_PARAM->{
                message = "Invalid command parameter"
            }
            STYL_NO_CARD_DETECTED->{
                message = "No card detected"
            }
            STYL_NO_RESPONSE->{
                message = "Reader no response"
            }
            STYL_NO_USB_PERMISSION->{
                message = "No USB permission"
            }
            STYL_ODA_ERROR_1->{
                message = "ODA not perform"
            }
            STYL_ODA_ERROR_2->{
                message = "ODA failed"
            }
            STYL_CARD_READ_ERROR->{
                message = "Card read error"
            }
            STYL_EXPIRED_CARD->{
                message = "Card Expired"
            }
            STYL_READER_BUSY->{
                message = "Reader busy"
            }
            STYL_NOT_ACCEPTED_OUTCOME->{
                message = "Card rejected"
            }
            NO_ERROR->{
                message = "NO ERROR"
            }
            AMT_NOT_SUFFICIENT->{
                message = "Amount not sufficient"
            }
            TORN_TRANSACTION->{
                message = "Torn Transaction"
            }
            ENTRY_NOT_FOUND->{
                message = "Entry not found"
            }
            EXIT_NOT_FOUND->{
                message = "Exit not found"
            }
            CSA_PRESENT_ALL_PASS_INVALID->{
                message = "Passes Invalid"
            }
            TIME_EXCEEDED->{
                message = "Time Exceed"
            }
            CARD_EXPIRED->{
                message = "Card Expired"
            }
            CARD_BLACKLISTED->{
                message = "Card Blacklisted"
            }
            OPERATOR_ID_MISMATCH->{
                message = "Operator ID mismatch"
            }
            FAILURE_FARE_CALC->{
                message = "Fare Calculation Error"
            }
            CSA_NOT_PRESENT->{
                message = "CSA not present"
            }
            BIN_PROHIBITED->{
                message = "BIN Not Allowed"
            }
            APPLICATION_BLOCKED->{
                message = "Application Blocked"
            }
            CARD_ALREADY_TAPPED->{
                message = "Card already tapped"
            }
            READER_FUNCTIONALITY_DISABLED->{
                message = "Reader Disabled"
            }
            FAILURE_ENTRY_VALIDATION->{
                message = "Entry Validation Error"
            }
            FAILURE_FARE_API->{
                message = "Fare Api Error"
            }
        }

        return message
    }
}