package com.shellinfo.common.utils

object Constants {
    const val SHARED_PREF_FILE_NAME = "shell_sp"
    const val ENCRYPTED_SHARED_PREF_FILE_NAME = "shell_enc_sp"
    const val CONNECTION_TIMEOUT = "60"

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
    const val ACQUIRER_ID ="AquirerId"
    const val OPERATOR_ID ="OperatorId"
    const val MINIMUM_BALANCE ="MinimumBalance"
    const val MINIMUM_FARE ="MinimumFare"
    const val PENALTY_AMOUNT ="PenaltyAmount"
    const val DB_STORAGE_DAYS ="dbStorageDays"
    const val POS_CONDITION_CODE ="posConditionCode"
    const val POS_ENTRY_MODE ="posEntryMode"
    const val CLIENT_ID ="ClientID"
    const val VENDOR_NAME ="vendorName"
    const val AUTHORIZATION_CODE ="AuthorizationCode"
    const val HEALTH_UPDATE_FREQUENCY ="healthUpdateFrequency"
    const val NETWORK_DOWN_TIME ="networkDowntime"
    const val HASHING ="hashing"
    const val CK_ID_IP_ADDRESS ="ckid_ipaddress"
    const val CC_IP_ADDRESS_API ="cc_ipaddress_api"
    const val CC_PORT ="cc_port"
    const val VERSION ="version"
    const val LOGGING_ON_OFF="logging_on_off"

    //ONBOARDING DATA
    const val READER_LOCATION = "READER_LOCATION"
    const val LINE_ID ="LineId"
    const val STATION_ID ="StationId"
    const val EQUIPMENT_ID ="EquipmentId"
    const val EQUIPMENT_GROUP_ID ="EquipmentGroupId"
    const val TERMINAL_ID ="TerminalID"
    const val ACTIVATION_CODE ="ActivationCode"
    const val IP_ADDRESS ="ipaddr"
    const val ECU_IP_ADDRESS ="ecuIpAddr"
    const val DOUBLE_TAP_THRESHOLD  = "doubleTapThreshHold"

}

object DBConstants{
    const val DATABASE_FILE_NAME = "SHELL_DB_COMMON"
    const val STATIONS_TABLE = "STATIONS_TABLE"
    const val ORDERS_TABLE = "ORDERS_TABLE"
    const val TICKET_BACKUP_TABLE = "TICKET_BACKUP_TABLE"

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
    const val TXN_STATUS_EXIT	=					0x00
    const val TXN_STATUS_ENTRY	=				    0x10
    const val TXN_STATUS_PENALTY=					0x20
    const val TXN_STATUS_ONE_TAP_TICKET	=		    0x30

    //Error Codes from CDAC Doc (NCMC Interface Specs)
    const val NO_ERROR=					                0			//NO errors found
    const val AMT_NOT_SUFFICIENT=		                1			//Amount not sufficient for Entry/Exit
    const val TORN_TRANSACTION=			                2			//Torn Transaction
    const val ENTRY_NOT_FOUND_CSA=		                3			//Entry not found in validation area in CSA
    const val EXIT_NOT_FOUND_CSA=		                4			//Exit not found in validation area in CSA
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
    const val FAILURE_ENTRY_VALIDATION			=		110		//Fail Entry Validation

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


    //CSA pre-defined values as per NCMC specs
    const val VERSION_NUMBER:Byte =                            0x31

    // Message ID for messages from Transit to Payment Application for Transaction
    const val MSG_ID_TRANSIT_VALIDATION_RUPAY_NCMC =            0x81
    const val MSG_ID_PAYMENT_APP_VERSION_REQUEST =              0x82
    const val MSG_ID_AMOUNT_RESPONSE =                          0x83
    const val MSG_ID_CSA_REQUEST =                              0x84
    const val MSG_ID_ERROR_TRANSACTION =                        0x85

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