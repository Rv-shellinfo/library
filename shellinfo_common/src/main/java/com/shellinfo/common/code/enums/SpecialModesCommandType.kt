package com.shellinfo.common.code.enums

enum class SpecialModesCommandType (val type: Int){

    EMERGENCY_MODE(1),
    INCIDENT_MODE(2),
    SEQ_OVERRIDE_MODE(3),
    TIME_OVERRIDE_MODE(4),
    STATION_CLOSE_MODE(5),
    FARE_BYPASS_ONE_MODE(6),
    FARE_BYPASS_TWO_MODE(7),
    DEVICE_CLOSE_MODE(8),
    MAINTENANCE_MODE(9);

    companion object {
        fun getSpecialModeCommand(type: Int?): SpecialModesCommandType? {
            return SpecialModesCommandType.values().find { it.type == type }
        }
    }
}