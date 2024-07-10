package com.shellinfo.common.utils

object Utils {


    /**
     * method to generate order id
     */
    fun generateOrderId(appVersion:String):String{

        //version code remove .
        val versionCode = appVersion.replace(".","")

        //create alpha numeric string
        val randomString = getAlphaNumericString(2)

        //get formatted date time
        val formattedDate = DateUtils.getDateInSpecificFormat("yyMMddHHmmss")

        //create order id
        val orderId= "AND"+versionCode+randomString+formattedDate

        return orderId
    }

    fun getAlphaNumericString(n: Int): String? {
        val AlphaNumericString = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz")

        // create StringBuffer size of AlphaNumericString
        val sb = StringBuilder(n)
        for (i in 0 until n) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            val index = (AlphaNumericString.length
                    * Math.random()).toInt()

            // add Character one by one in end of sb
            sb.append(AlphaNumericString[index])
        }
        return sb.toString()
    }
}