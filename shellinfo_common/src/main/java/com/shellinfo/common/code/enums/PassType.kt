package com.shellinfo.common.code.enums

enum class PassType(val passCode: Byte) {
    SV(0x00),
    DAILY_PASS(0x20),
    WEEKEND(0x21),
    WEEKLY(0x22),
    FORTNIGHTLY(0x23),
    MONTHLY(0x24),
    QUARTERLY(0x25),
    HALF_YEARLY(0x26),
    YEARLY(0x27),
    EVENT(0x28),
    HOLIDAY(0x29),
    TRIPS_30(0x40),
    TRIPS_45(0x41),
    TRIPS_60(0x42),
    TRIPS_90(0x43),
    ZONE_1(0x60),
    ZONE_2(0x61),
    ZONE_3(0x62),
    ZONE_4(0x63),
    ZONE_5(0x64),
    ZONE_6(0x65),
    ZONE_7(0x66),
    ZONE_8(0x67),
    ZONE_9(0x68),
    ZONE_10(0x69),
    ZONE_11(0x6A),
    ZONE_12(0x6B);

    companion object {
        fun fromPassCode(code: Byte): PassType? {
            return values().find { it.passCode == code }
        }

        // Method to get the name of the pass from its byte value (pass code)
        fun getPassNameByCode(code: Byte): String? {
            return values().find { it.passCode == code }?.name
        }

        // Method to get the passCode in hex string format
        fun getPassCodeHex(code: Byte): String {
            return "0x%02X".format(code)
        }
    }
}
