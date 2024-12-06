package com.shellinfo.common.data.local.data.mqtt

import com.squareup.moshi.JsonClass


interface MqttData

/**
 * OTA Message data from the MQTT
 */
@JsonClass(generateAdapter = true)
data class OtaUpdateMessage(
    val fileName: String,
    val version: String,
    val ftpPath: String,
    val md5FileHash: String,
):MqttData



/**
 * Log status data from the MQTT
 */
@JsonClass(generateAdapter = true)
data class LogStatusMessage(
    val logStatus: String
):MqttData

/**
 * Master config data from the MQTT
 */
@JsonClass(generateAdapter = true)
data class ConfigUpdateMessage(
    val baseUrl: String,
    val port: String,
    val apiEndPoint: String,
    val fileName: String,
    val version: String,
    val ftpPath: String
):MqttData

/**
 * Firmware update data from the MQTT
 */
@JsonClass(generateAdapter = true)
data class FirmwareUpdateMessage(
    val fileName: String,
    val version: String,
    val ftpPath: String
):MqttData

/**
 * STYL Reader key injection data from the MQTT
 */
@JsonClass(generateAdapter = true)
data class KeyInjectionMessage(
    val capkKeyFileName: String,
    val entryPointKeyFileName: String,
    val processingKeyFileName: String,
    val terminalKeyFileName: String,
    val rupayKeyFileName: String,
    val emvInfoFileName: String,
    val version: String,
    val ftpPath: String,
):MqttData


@JsonClass(generateAdapter = true)
data class DeviceControlMessage(
    val commandType: String,
    val commandTypeId: Int,
    val commandStatus: String,
    val message: String,
):MqttData

@JsonClass(generateAdapter = true)
data class SpecialModeMessage(
    val commandType: String,
    val commandTypeId: Int,
    val commandStatus: String,
    val message: String,
):MqttData

@JsonClass(generateAdapter = true)
data class ParameterMessage(
    val parameterType: String,
    val baseUrl: String,
    val port: String,
    val apiEndPoint: String,
    val fileName: String,
    val version: String,
    val ftpPath: String,
    val md5FileHash: String,
):MqttData


@JsonClass(generateAdapter = true)
data class SleDatabaseMessage(
    val tableName: String,
    val tableId: Int,
    val dataType: Int,
    val expiryDateTime: String,
    val md5FileHash: String,
    val baseUrl: String,
    val port: String,
    val apiEndPoint: String,
    val fileName: String,
    val version: String,
    val ftpPath: String,
):MqttData


@JsonClass(generateAdapter = true)
data class SleDynamicMessage(
    val message: String,
    val cancelable: Boolean,
    val md5FileHash: String,
    val status:String
):MqttData


