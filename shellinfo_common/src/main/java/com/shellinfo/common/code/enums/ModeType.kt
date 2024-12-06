package com.shellinfo.common.code.enums

enum class ModeType(val type: Int) {

    IN_SERVICE_MODE(0),
    EMERGENCY_MODE(1),
    INCIDENT_MODE(2),
    SEQ_OVERRIDE_MODE(3),
    TIME_OVERRIDE_MODE(4),
    STATION_CLOSE_MODE(5),
    FARE_BYPASS_ONE_MODE(6),
    FARE_BYPASS_TWO_MODE(7),
    DEVICE_CLOSE_MODE(8),
    MAINTENANCE_MODE(9),
    OUT_OF_SERVICE_MODE(10),
    POWER_SAVING_MODE(11),
    TEST_MODE(12),
    FAILURE_MODE(13),
    REBOOT(14),
    SHUTDOWN(15);


    companion object {
        fun getDeviceMode(type: Int?): ModeType? {
            return ModeType.values().find { it.type == type }
        }
    }
}