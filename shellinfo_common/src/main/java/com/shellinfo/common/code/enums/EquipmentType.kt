package com.shellinfo.common.code.enums

enum class EquipmentType(val type: String) {

    TOM("TOM"),
    TVM("TVM"),
    TR("TR"),
    PTD("PTD"),
    VALIDATOR("VALIDATOR"),
    ALL("ALL");


    companion object {
        fun fromEquipment(type: String?): EquipmentType? {
            return EquipmentType.values().find { it.type == type }
        }
    }
}