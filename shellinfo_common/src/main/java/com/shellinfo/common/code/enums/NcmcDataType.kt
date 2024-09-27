package com.shellinfo.common.code.enums

enum class NcmcDataType(val type: String) {
    CSA("CSA_DATA"),
    OSA("OSA_DATA"),
    ALL("ALL"),
    NONE("NO_DATA");


    companion object {
        fun fromNcmcDataType(type: String?): NcmcDataType? {
            return NcmcDataType.values().find { it.type == type }
        }
    }
}