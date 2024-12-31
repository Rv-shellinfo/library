package com.shellinfo.common.code.enums

enum class TransactionType(val type:Int) {
    SALE(103),
    OVER_TRAVEL(106),
    REFUND(107),
    OVER_STAY(108),
    FREE_EXIT(109),
    PAID_EXIT(110);

    companion object {

        fun getDescriptionByType(type: Int): String {
            return when (type) {
                103 -> "Sale"
                106 -> "Over Travel"
                107 -> "Refund"
                108 -> "Over Stay"
                109 -> "Free Exit"
                110 -> "Paid Exit"
                else -> "Unknown Transaction"
            }
        }

        fun fromType(type: Int): TransactionType? {
            return values().find { it.type == type }
        }


    }


}