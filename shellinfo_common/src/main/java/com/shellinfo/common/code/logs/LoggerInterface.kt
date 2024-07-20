package com.shellinfo.common.code.logs

interface LoggerInterface {

    fun initLogger()

    fun startLogging()

    fun stopLogging()

    fun logData(tag: String, message: String)
}