package com.shellinfo.common.code.enums

enum class TransactionType(val type:Int) {
    SALE(103),
    REFUND(107),
    FREE_EXIT(109),
    PAID_EXIT(110)
}