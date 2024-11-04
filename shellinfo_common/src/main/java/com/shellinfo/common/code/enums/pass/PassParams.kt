package com.shellinfo.common.code.enums.pass

enum class PassParams(val value: Int) {
    UNLIMITED_WITH_ZONE(0b0000),                  // 0
    UNLIMITED_WITHOUT_ZONE(0b0001),               // 1
    FIX_TRIPS_WITH_ZONE(0b0010),                  // 2
    FIX_TRIPS_WITHOUT_ZONE(0b0011),               // 3
    FIX_DAILY_TRIPS_WITH_ZONE(0b0100),            // 4
    FIX_DAILY_LIMIT_WITHOUT_ZONE(0b0101),         // 5
    FIX_TRIPS_FIX_DAILY_LIMIT_WITH_ZONE(0b0110),  // 6
    FIX_TRIPS_FIX_DAILY_LIMIT_WITHOUT_ZONE(0b0111),// 7
    FIX_TRIPS_FIX_STATIONS(0b1000),               // 8
    FIX_DAILY_LIMIT_FIX_STATIONS(0b1001),         // 9
    FIX_TRIPS_FIX_DAILY_LIMIT_FIX_STATIONS(0b1010);// 10
}