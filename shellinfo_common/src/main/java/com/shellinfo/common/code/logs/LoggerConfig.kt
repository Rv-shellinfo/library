package com.shellinfo.common.code.logs

data class LoggerConfig(

    var logLocalPath:String? = "log",

    var logServerPath: String? = logLocalPath,

    var logLocalFileName:String? = "log_file",

    var logServerFileName: String? = logLocalFileName,

    var logDateFormat:String? = "dd-MM-yyyy-HH:mm:ss",

    var ftpHostAddress:String,

    var ftpHostPort:String,

    var ftpUser:String? ="",

    var ftpPassword:String?="",

    var logUploadFrequency: Int? = 10,


    )
