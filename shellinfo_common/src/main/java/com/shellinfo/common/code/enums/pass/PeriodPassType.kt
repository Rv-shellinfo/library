package com.shellinfo.common.code.enums.pass

enum class PeriodPassType(val passType: Int) {

    SV(0b00000),            // 0 in binary (5 bits)
    DAILY_PASS(0b00001),     // 1 in binary (5 bits)
    WEEKEND(0b00010),        // 2 in binary (5 bits)
    WEEKLY(0b00011),         // 3 in binary (5 bits)
    MONTHLY(0b00100),        // 4 in binary (5 bits)
    QUARTERLY(0b00101),      // 5 in binary (5 bits)
    HALF_YEARLY(0b00110),    // 6 in binary (5 bits)
    YEARLY(0b00111),         // 7 in binary (5 bits)
    EVENT(0b01000),          // 8 in binary (5 bits)
    HOLIDAY(0b01001);        // 9 in binary (5 bits)

    companion object {
        fun fromPeriodPassCode(code: Int): PeriodPassType? {
            return PeriodPassType.values().find { it.passType == code }
        }

        // Method to get the name of the pass from its byte value (pass code)
        fun getPeriodPassNameByCode(code: Int): String? {
            return PeriodPassType.values().find { it.passType == code }?.name
        }
    }
}