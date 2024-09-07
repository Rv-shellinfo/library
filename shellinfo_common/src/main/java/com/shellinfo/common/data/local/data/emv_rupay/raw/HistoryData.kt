package com.shellinfo.common.data.local.data.emv_rupay.raw

/**
 * History Data for CSA (106 to 140)
 * As Four History Data can be saved i.e. 0 to 4 index in a list
 * every property have 4 index i.e. H1,H2,H3,H4 (H means History)
 *
 * @property terminalInfo: H1(106,118) H2(140,152) H3(174,186) H4(208,220) It specify Acquirer,Operator,Terminal ID (Combination)
 * @property txnDateTime: H1(118,124) H2(152,158) H3(186,192) H4(220,226)
 * @property txnSeqNumber: H1(124,128) H2(158,162) H3(192,196) H4(226,230)
 * @property txnAmount: H1(128,132) H2(162,166) H3(196,200) H4(230,234)
 * @property cardBalance: H1(132,137) H2(166,171) H3(200,205) H4(234,239)
 * @property txnStatus: H1(137,138) H2(171,172) H3(205,206) H4(239,240)
 * @property rfu: H1(138,140) H2(172,174) H3(206,208) H4(240,242)
 *
 */
data class HistoryData(

    /**
     * It specify Acquirer,Operator,Terminal ID (Combination)
     * @see TerminalData
     */
    var terminalInfo: TerminalData?= null ,

    /**
     * It represents the date & time of the transaction.
     * This field value will be stored with
     * respect to card effective date.
     */
    var txnDateTime:String = "",

    /**
     * It is a terminal transaction unique number and will be updated if any non-zero debit
     * operation is performed.
     */
    var txnSeqNumber:String = "",

    /**
     * This represents the fare which has been deducted by the terminal from the card
     * balance.
     */
    var txnAmount:String = "",

    /**
     * It represents the value corresponding to the amount available inside the card before a
     * debit transaction takes place on a terminal.
     */
    var cardBalance:String = "",

    /**
     * This will give the information about the transaction status as given in Validation Data
     * @see ValidationData.txnStatus
     */
    var txnStatus:String = "",

    /**
     * Reserved for Future Use
     */
    var rfu:String = "",

    )
