package com.shellinfo.common.utils

object Constants {
    const val SHARED_PREF_FILE_NAME = "shell_sp"
    const val ENCRYPTED_SHARED_PREF_FILE_NAME = "shell_enc_sp"
    const val CONNECTION_TIMEOUT = "60"
    const val ACTION_USB_PERMISSION = "com.shellinfo.common.USB_PERMISSION"

}

object SpConstants{

    //App's related constants
    const val APP_NAME="APPLICATION_NAME"
    const val APP_ID="APPLICATION_ID"
    const val APP_VERSION_CODE="APPLICATION_VERSION_CODE"
    const val APP_VERSION_NAME="APPLICATION_VERSION_NAME"
    const val APP_TYPE="APP_TYPE"
    const val DEVICE_TYPE="DEVICE_TYPE"
    const val DEVICE_SERIAL="DEVICE_SERIAL"

    //API constants
    const val API_MODE ="API_MODE"
    const val API_PORT ="API_PORT"
    const val API_HTTP_TYPE ="API_HTTP_TYPE"
    const val API_BASE_URL ="API_BASE_URL"
    const val API_FULL_BASE_URL ="API_FULL_BASE_URL"
    const val API_TOKEN ="API_TOKEN"
    const val MERCHANT_ID ="MERCHANT_ID"


    // SP constants for LOGS
    const val LOG_LOCAL_FILE_PATH = "LOG_LOCAL_FILE_PATH"
    const val LOG_SERVER_FILE_PATH = "LOG_SERVER_FILE_PATH"
    const val LOG_LOCAL_FILE_NAME = "LOG_LOCAL_FILE_NAME"
    const val LOG_SERVER_FILE_NAME = "LOG_SERVER_FILE_NAME"
    const val LOG_DATE_FORMAT = "LOG_DATE_FORMAT"
    const val FTP_HOST_ADDRESS = "FTP_HOST_ADDRESS"
    const val FTP_HOST_PORT = "FTP_HOST_PORT"
    const val FTP_USER = "FTP_USER"
    const val FTP_PASSWORD = "FTP_PASSWORD"
    const val LOG_UPLOAD_FREQUENCY = "LOG_UPLOAD_FREQUENCY"
    const val LOCAL_LOG_FILE_DIRECTORY = "LOCAL_LOG_FILE_DIRECTORY"
    const val SC_IP_ADDRESS_API ="SC_IP_ADDRESS_API"
    const val SC_PORT ="SC_PORT"
    const val NEW_APK_FILE_NAME = "NEW_APK_FILE_NAME"
    const val UPLOAD_TIME_FREQUENCY = "UPLOAD_TIME_FREQUENCY"

    //BF200 Device READER LOCATIONS
    const val ENTRY_SIDE ="ENTRY"
    const val EXIT_SIDE ="EXIT"
    const val ENTRY_EXIT ="ENTRY_EXIT"

    //ENTRY EXIT OVERRIDE ON/OFF
    const val ENTRY_EXIT_OVERRIDE ="ENTRY_EXIT_OVERRIDE"

    //CONFIG DATA
    const val ACQUIRER_ID ="ACQUIRER_ID"
    const val OPERATOR_ID ="OPERATOR_ID"
    const val OPERATOR_NAME_ID ="OPERATOR_NAME_ID"
    const val MINIMUM_BALANCE ="MINIMUM_BALANCE"
    const val MINIMUM_FARE ="MINIMUM_FARE"
    const val PENALTY_AMOUNT ="PENALTY_AMOUNT"
    const val DB_STORAGE_DAYS ="DB_STORAGE_DAYS"
    const val POS_CONDITION_CODE ="POS_CONDITION_CODE"
    const val POS_ENTRY_MODE ="POS_ENTRY_MODE"
    const val CLIENT_ID ="CLIENT_ID"
    const val VENDOR_NAME ="VENDOR_NAME"
    const val AUTHORIZATION_CODE ="AUTHORIZATION_CODE"
    const val HEALTH_UPDATE_FREQUENCY ="HEALTH_UPDATE_FREQUENCY"
    const val NETWORK_DOWN_TIME ="NETWORK_DOWN_TIME"
    const val HASHING ="HASHING"
    const val CK_ID_IP_ADDRESS ="CK_ID_IP_ADDRESS"
    const val CC_IP_ADDRESS_API ="CC_IP_ADDRESS_API"
    const val CC_PORT ="CC_PORT"
    const val CONFIG_VERSION ="CONFIG_VERSION"
    const val LOGGING_ON_OFF="LOGGING_ON_OFF"

    //ONBOARDING DATA
    const val READER_LOCATION = "READER_LOCATION"
    const val LINE_ID ="LINE_ID"
    const val STATION_ID ="STATION_ID"
    const val EQUIPMENT_ID ="EQUIPMENT_ID"
    const val EQUIPMENT_GROUP_ID ="EQUIPMENT_GROUP_ID"
    const val TERMINAL_ID ="TERMINAL_ID"
    const val ACTIVATION_CODE ="ACTIVATION_CODE"
    const val IP_ADDRESS ="IP_ADDRESS"
    const val ECU_IP_ADDRESS ="ECU_IP_ADDRESS"
    const val DOUBLE_TAP_THRESHOLD  = "DOUBLE_TAP_THRESHOLD"
    const val BANK_TID ="Bank TID"
    const val BANK_MID= "BANK_MID"


    //NCMC Service types
    const val COMMON_SERVICE_ID ="COMMON_SERVICE_ID"
    const val OPERATOR_SERVICE_ID ="OPERATOR_SERVICE_ID"

    //PASS Constants
    const val IS_TODAY_HOLIDAY= "IS_TODAY_HOLIDAY"
    const val IS_TODAY_EVENT = "IS_TODAY_EVENT"

    //Terminal sequence number
    const val TRANSACTION_SEQ_NUMBER= "TRANSACTION_SEQ_NUMBER"

    //Current Mode
    const val CURRENT_MODE= "CURRENT_MODE"

    //OTA keys
    const val IS_APP_UPDATED= "IS_APP_UPDATED"
    const val UPDATE_ACK_MESSAGE="UPDATE_ACK_MESSAGE"
    const val APP_SERVER_VERSION="APP_SERVER_VERSION"


}

object DBConstants{
    const val DATABASE_FILE_NAME = "SHELL_DB_COMMON"
    const val STATIONS_TABLE = "STATIONS_TABLE"
    const val ORDERS_TABLE = "ORDERS_TABLE"
    const val TICKET_BACKUP_TABLE = "TICKET_BACKUP_TABLE"
    const val PASS_TABLE = "PASS_TABLE"
    const val TRIP_LIMITS_TABLE = "TRIP_LIMITS_TABLE"
    const val DAILY_LIMITS_TABLE = "DAILY_LIMITS_TABLE"
    const val ZONE_TABLE = "ZONE_TABLE"
    const val PURCHASE_PASS_TABLE = "PURCHASE_PASS_TABLE"
    const val ENTRY_TRANSACTION_TABLE = "ENTRY_TRANSACTION_TABLE"
    const val EXIT_TRANSACTION_TABLE = "EXIT_TRANSACTION_TABLE"

}

object IPCConstants{

    //IPC MESSAGE IDENTIFIER
    const val PAYMENT_MESSAGE=     100
    const val TRANSIT_MESSAGE = 101

    //IPC MESSAGE TYPES
    const val PAYMENT_APP_MESSAGE=  "PAYMENT_APP_MESSAGE"
    const val TRANSIT_APP_MESSAGE=  "TRANSIT_APP_MESSAGE"

    //Transaction Base Types
    const val EMV_TRX = 0
    const val RUPAY_SERVICE_TRX = 1


    //Transaction Status
    const val TRX_SUCCESS = 0
    const val TRX_FAILED = 1


    //Error Code STYL Reader Specific
    const val STYL_NO_ERROR	=			            0x00			    //Command executed successfully
    const val STYL_COMMAND_EXE_FAILED	=			0xFF			    //Command execution failed
    const val STYL_INVALID_COMMAND_PARAM	=		0xFE			    //Invalid value of a command parameter
    const val STYL_NO_CARD_DETECTED	=			    0xFB			    //No card detected or card removed
    const val STYL_NO_RESPONSE	=			        0xF8			    //Reader not found/ No response
    const val STYL_NO_USB_PERMISSION	=			0xEB			    //No USB permission
    const val STYL_ODA_ERROR_1	=			        0xE2			    //ODA not perform
    const val STYL_ODA_ERROR_2	=			        0xE1			    //ODA failed
    const val STYL_CARD_READ_ERROR	=			    0xDF			    //Card read error
    const val STYL_EXPIRED_CARD	=			        0xDE			    //Expired card
    const val STYL_READER_BUSY	=			        0xF9			    //Reader busy
    const val STYL_NOT_ACCEPTED_OUTCOME	=			0xFD			    //Not accepted outcome (When the card is rejected have to check CardType in EMV Results)


    //CSA pre-defined values as per NCMC specs
    const val TXN_STATUS_EXIT	=					0
    const val TXN_STATUS_ENTRY	=				    1
    const val TXN_STATUS_PENALTY=					2
    const val TXN_STATUS_ONE_TAP_TICKET	=		    3

    //Error Codes from CDAC Doc (NCMC Interface Specs)
    const val NO_ERROR=					                0			//NO errors found
    const val AMT_NOT_SUFFICIENT=		                1			//Amount not sufficient for Entry/Exit
    const val TORN_TRANSACTION=			                2			//Torn Transaction
    const val ENTRY_NOT_FOUND=		                    3			//Entry not found in validation area in CSA
    const val EXIT_NOT_FOUND=		                    4			//Exit not found in validation area in CSA
    const val CSA_PRESENT_ALL_PASS_INVALID=	            5		    //service area present but all pass invalid
    const val TIME_EXCEEDED=			                6			//Time Exceed
    const val CARD_EXPIRED=			                    7			//Card Expired

    //Error Codes Operator Specific
    const val OPERATOR_ID_MISMATCH	=			        101			    //Operator ID mismatch
    const val CARD_BLACKLISTED		=			        102			    //Card is Blacklisted
    const val FAILURE_FARE_CALC		=			        103			    //Unable to calculate Fare
    const val CSA_NOT_PRESENT		=				    104			//CSA is not present in Rupay Card
    const val BIN_PROHIBITED		=				    105			//BIN is not allowed
    const val CLOSED_LOOP_CARD_PRESENTED	=		    106			//Closed loop card presented on reader
    const val APPLICATION_BLOCKED			=		    107			//Application Blocked
    const val CARD_ALREADY_TAPPED			=		    108			//Card already tapped
    const val READER_FUNCTIONALITY_DISABLED			=	109			//Reader in Maintenance mode/Reader Off
    const val FAILURE_ENTRY_VALIDATION			=		110		    //Fail Entry Validation
    const val FAILURE_FARE_API			=		        111		    //Fail Entry Validation
    const val ALL_PASSES_VALID                  =       112         //All passes valid

    //CSA pre-defined product type (NCMC Interface Specs)
    const val PROD_TYPE_SINGLE_JOURNEY:Byte  =			    0x00
    const val PROD_TYPE_DISCOUNTED_FARE:Byte  =			    0x1F
    const val PROD_TYPE_PASS  =			                    0xFF

    // Message ID for messages from Payment to Transit Application
    const val MSG_ID_TRX_DATA_RUPAY_NCMC  =               0x01
    const val MSG_ID_TRX_STATUS_RUPAY_NCMC =              0x02
    const val MSG_ID_TRX_DATA_EMV =                       0x03
    const val MSG_ID_PAYMENT_APP_VERSION_DATA =           0x04
    const val MSG_ID_ICC_DATA =                           0x05
    const val MSG_ID_AMOUNT_REQUEST =                     0x06
    const val MSG_ID_STYL_ERROR =                         0x07
    const val MSG_ID_CREATE_OSA_ACK =                     0x08
    const val MSG_ID_REMOVE_PENALTY_ACK =                 0x09
    const val MSG_ID_REMOVE_PENALTY_DATA_ACK =            0x10
    const val MSG_ID_NO_DATA_ERROR =                      0x11
    const val MSG_ID_CREATE_PASS_ACK =                    0x12
    const val MSG_ID_LOGS =                               0x13


    //CSA pre-defined values as per NCMC specs
    const val VERSION_NUMBER:Byte =                            0x31

    // Message ID for messages from Transit to Payment Application for Transaction
    const val MSG_ID_DELETE_OSA_DATA =                          0x71
    const val MSG_ID_DELETE_CSA_DATA =                          0x72
    const val MSG_ID_TRANSIT_VALIDATION_RUPAY_NCMC =            0x81
    const val MSG_ID_TRANSIT_VALIDATION_FAIL_RUPAY_NCMC =       0x82
    const val MSG_ID_AMOUNT_RESPONSE =                          0x83
    const val MSG_ID_CSA_REQUEST =                              0x84
    const val MSG_ID_ERROR_TRANSACTION =                        0x85
    const val MSG_ID_STOP_CARD_DETECTION =                      0x86
    const val MSG_ID_ONE_TIME_READ_CARD_REQUEST =               0x87
    const val MSG_ID_ONE_TIME_READ_CARD_REQUEST_ACK =           0x88
    const val MSG_ID_CONTINUES_READ_CARD_REQUEST_ACK =          0x89
    const val MSG_ID_CREATE_OSA_SERVICE =                       0x90
    const val MSG_ID_REMOVE_PENALTY =                           0x91
    const val MSG_ID_REMOVE_PENALTY_DATA =                      0x92
    const val MSG_ID_START_CARD_DETECTION =                     0x93
    const val MSG_ID_CREATE_PASS =                              0x94
    const val MSG_ID_CREATE_PASS_DATA =                         0x95
    const val MSG_ID_ABORT_OSA_TRANSACTION =                    0x96


    // Message ID for messages from Transit to Payment Application for Reader Commands
    const val MSG_ID_NFC_COMMAND =                               0xF1
    const val MSG_ID_LED_COMMAND =                               0xF2
    const val MSG_ID_BUZZER_COMMAND =                            0xF3
    const val MSG_ID_FIRMWARE_COMMAND =                          0xF4
    const val MSG_ID_EMV_CONFIG_COMMAND =                        0xF5
    const val MSG_ID_RUPAY_CONFIG_COMMAND =                      0xF6
    const val MSG_ID_OTA_COMMAND =                               0xF7
    const val MSG_ID_BOOT_COMMAND =                              0xF8
    const val MSG_ID_AID_DENY_LIST_UPDATE_COMMAND =              0xF9
    const val MSG_ID_CUP_DENY_LIST_UPDATE_COMMAND =              0xF10


    const val TRX_STATUS_MASK = 0b11110000 // Mask for the upper 4 bits (trxStatus)
    const val RFU_MASK = 0b00001111 // Mask for the lower 4 bits (RFU)

    // Message for OSA Service creation
    const val SERVICE_OSA_CREATED_SUCCESS=                      0x00
    const val SERVICE_OSA_ALREADY_EXIST=                        0x11
    const val SERVICE_OSA_UNKNOWN_ERROR=                        0x12

    //CSA pre-defined LANGUAGE values as per NCMC specs
    const val LANGUAGE_MASK =                           0b00011111
    const val LANG_ENGLISH =                               0b00000
    const val LANG_HINDI =                                 0b00001
    const val LANG_BENGALI =                               0b00010
    const val LANG_MARATHI =                               0b00011
    const val LANG_TELUGU =                                0b00100
    const val LANG_TAMIL =                                 0b00101
    const val LANG_GUJARATI =                              0b00110
    const val LANG_URDU =                                  0b00111
    const val LANG_KANNADA =                               0b01000
    const val LANG_ODIA =                                  0b01001
    const val LANG_MALAYALAM =                             0b01010
    const val LANG_PUNABI =                                0b01011
    const val LANG_SANSKRIT =                              0b01100
    const val LANG_ASSAMESE =                              0b01101
    const val LANG_MAITHILI =                              0b01110
    const val LANG_SANTALI =                               0b01111
    const val LANG_KASHMIRI =                              0b10000
    const val LANG_NEPALI =                                0b10001
    const val LANG_SINDHI =                                0b10010
    const val LANG_DOGRI =                                 0b10011
    const val LANG_KONKANI =                               0b10100
    const val LANG_MANIURI =                               0b10101
    const val LANG_BODO =                                  0b10110

    //Card Types
    const val RUPAY_PREPAID = "0D"
    const val MASTER_CARD = "02h"
    const val VISA_CARD = "03h"

    //ENTRY EXIT OVERRIDE
    const val ENTRY_EXIT_OVERRIDE_OFF = "0"
    const val ENTRY_EXIT_OVERRIDE_ON = "1"

}

object DeviceVendors{

    const val TOM_QR_SCANNER_VENDOR_ID = 7851
    const val TVM_QR_SCANNER_VENDOR_ID = 7852
    const val VALIDATOR_QR_SCANNER_VENDOR_ID = 7853
    const val EMV_READER_VENDOR_ID = 11563
    const val POS_DEVICE_VENDOR_ID = 3725
    const val PDU_DEVICE_TOM_VENDOR_ID = 9721
    const val PDU_DEVICE_TVM_VENDOR_ID = 9722
    const val PRINTER_DEVICE_TOM_VENDOR_ID = 9799
    const val PRINTER_DEVICE_TVM_VENDOR_ID = 9788
    const val ECU_VENDOR_ID = 1659
}