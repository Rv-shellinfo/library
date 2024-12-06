package com.shellinfo.common.code.enums

enum class ReaderLocationType (val type: Int){

    EXIT_SIDE(0),
    ENTRY_SIDE(1),
    ENTRY_EXIT_SIDE(2);

    companion object {
        fun getReaderLocation(type: Int?): ReaderLocationType? {
            return ReaderLocationType.values().find { it.type == type }
        }
    }
}