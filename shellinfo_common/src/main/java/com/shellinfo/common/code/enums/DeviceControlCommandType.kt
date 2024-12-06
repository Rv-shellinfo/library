package com.shellinfo.common.code.enums

enum class DeviceControlCommandType (val type: Int){

    IN_SERVICE_MODE(1),
    OUT_OF_SERVICE_MODE(2),
    MAINTENANCE_MODE(3),
    POWER_SAVING_MODE(4),
    TEST_MODE(5),
    FAILURE_MODE(6),
    REBOOT(7),
    SHUTDOWN(8);

    companion object {
        fun getDeviceControlCommand(type: Int?): DeviceControlCommandType? {
            return DeviceControlCommandType.values().find { it.type == type }
        }
    }
}