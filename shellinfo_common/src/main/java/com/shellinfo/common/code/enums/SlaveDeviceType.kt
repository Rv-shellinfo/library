package com.shellinfo.common.code.enums

enum class SlaveDeviceType (val type: Int) {

    EMV_READER(1),
    QR_READER_TOM(2), // Validator, tvm , tom side qr reader
    QR_READER_TVM(3), // Validator, tvm , tom side qr reader
    QR_READER_VALIDATOR(4), // Validator, tvm , tom side qr reader
    POS_DEVICE(5),
    PDU_DEVICE_TOM(6),
    PDU_DEVICE_TVM(7),
    PRINTER_TOM(8),
    PRINTER_TVM(9),
    ECU(10);

    companion object {
        fun fromSlaveDeviceType(type: Int?): SlaveDeviceType? {
            return SlaveDeviceType.values().find { it.type == type }
        }
    }
}