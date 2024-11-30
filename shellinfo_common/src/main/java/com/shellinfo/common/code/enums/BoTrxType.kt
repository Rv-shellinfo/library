package com.shellinfo.common.code.enums

/**
 * Enums to define Back Office Transaction Types
 */
enum class BoTrxType(val type:Int) {

    METRO_ENTRY(1),
    METRO_EXIT(2),
    GATE_CUTOFF(3),
    ADD_VALUE(4),
    CREATE_SERVICE(5),
    BLACKLIST(6),
    ADD_PASS(7),
    REFUND_PASS(8),
    ADMIN_HANDLING(9),
    CARD_ISSUANCE(10),
    BALANCE_UPDATE(11),
    VOID(12),
    SALE_QR_CASH(13),
    SALE_QR_NON_CASH(14),
    OVER_STAY_PENALTY_CASH(15),
    OVER_STAY_PENALTY_NON_CASH(16),
    ENTRY_USED(17)
}