package com.shellinfo.common.code.enums.pass

enum class TripPassType(val passType: Int) {

    TRIPS_30(0b00000),      // 0 in binary (5 bits)
    TRIPS_45(0b00001),      // 1 in binary (5 bits)
    TRIPS_60(0b00010),      // 2 in binary (5 bits)
    TRIPS_90(0b00011);      // 3 in binary (5 bits)


    companion object {
        fun fromTripPassCode(code: Int): TripPassType? {
            return TripPassType.values().find { it.passType == code }
        }

        // Method to get the name of the pass from its byte value (pass code)
        fun getTripPassNameByCode(code: Int): String? {
            return TripPassType.values().find { it.passType == code }?.name
        }
    }
}