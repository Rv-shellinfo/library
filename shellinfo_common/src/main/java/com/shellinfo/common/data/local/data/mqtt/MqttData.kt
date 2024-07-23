package com.shellinfo.common.data.local.data.mqtt

import com.squareup.moshi.JsonClass


interface MqttData

/**
 * OTA Message data from the MQTT
 */
@JsonClass(generateAdapter = true)
data class OtaUpdateMessage(
    val file_name: String,
    val version: String,
    val ftp_path: String,
    val md5FileHash: String,
    val activationDateTime: String
):MqttData



/**
 * Log status data from the MQTT
 */
@JsonClass(generateAdapter = true)
data class LogStatusMessage(
    val log_status: String,
    val activationDateTime: String
):MqttData

/**
 * Master config data from the MQTT
 */
@JsonClass(generateAdapter = true)
data class ConfigUpdateMessage(
    val base_url: String,
    val port: String,
    val api_end_point: String,
    val file_name: String,
    val version: String,
    val ftp_path: String,
    val activationDateTime: String
):MqttData

/**
 * Firmware update data from the MQTT
 */
@JsonClass(generateAdapter = true)
data class FirmwareUpdateMessage(
    val file_name: String,
    val version: String,
    val ftp_path: String,
    val activationDateTime: String
):MqttData

/**
 * STYL Reader key injection data from the MQTT
 */
@JsonClass(generateAdapter = true)
data class KeyInjectionMessage(
    val capk_key_file_name: String,
    val entry_point_key_file_name: String,
    val processing_key_file_name: String,
    val terminal_key_file_name: String,
    val rupay_key_file_name: String,
    val emvinfo_file_name: String,
    val version: String,
    val ftp_path: String,
    val activationDateTime: String,
):MqttData


@JsonClass(generateAdapter = true)
data class DeviceControlMessage(
    val command_type: String,
    val command_status: String,
    val applyDateTime: String,
):MqttData

@JsonClass(generateAdapter = true)
data class SpecialModeMessage(
    val command_type: String,
    val command_status: String,
    val applyDateTime: String,
):MqttData

@JsonClass(generateAdapter = true)
data class ParameterMessage(
    val parameter_type: String,
    val file_name: String,
    val version: String,
    val ftp_path: String,
    val md5FileHash: String,
    val activationDateTime: String,
):MqttData


