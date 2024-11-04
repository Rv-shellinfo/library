package com.shellinfo.common.code.enums.pass

enum class PassType(val passType: Int) {
    PERIOD(0b000),  // 0 in binary (3 bits)
    TRIPS(0b001),   // 1 in binary (3 bits)
    ZONE(0b010);    // 2 in binary (3 bits)


    companion object {
        fun fromPassCode(code: Int): PassType? {
            return PassType.values().find { it.passType == code }
        }

        // Method to get the name of the pass from its byte value (pass code)
        fun getPassNameByCode(code: Int): String? {
            return PassType.values().find { it.passType == code }?.name
        }
    }
}