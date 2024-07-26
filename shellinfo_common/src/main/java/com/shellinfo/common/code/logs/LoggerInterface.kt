package com.shellinfo.common.code.logs

interface LoggerInterface {

    fun initLogger()

    fun startLogging(localLogs:Boolean, serverLogs:Boolean)

    fun stopLogging(localLogs:Boolean, serverLogs:Boolean)

    fun logData(tag: String, message: String)

    fun logError(teg:String, error:Throwable)
}