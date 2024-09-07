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


    fun bin2hex(bin: String, len: Int): String {
        // Ensure the input string is a valid hexadecimal string
        require(bin.length == len * 2) { "Input string length must be twice the byte array length." }

        val result = StringBuilder(len * 2) // Each byte will be converted into two hex characters
        var index = 0

        while (index < len * 2) {
            // Extract two hex characters at a time (each representing 4 bits)
            val firstNibble = bin[index].digitToInt(16) // First 4 bits
            val secondNibble = bin[index + 1].digitToInt(16) // Second 4 bits

            // Convert each nibble to its corresponding hex character
            result.append(nib2chr(firstNibble))
            result.append(nib2chr(secondNibble))

            index += 2 // Move to the next pair of hex characters
        }

        return result.toString()
    }

    fun nib2chr(nibble: Int): Char {
        // Convert nibble (0–15) to its corresponding hex character (0–9 or A–F)
        return "0123456789ABCDEF"[nibble]
    }

    fun numToBin(bin: ByteArray, num: Long, len: Int): Int {
        var currentNum = num
        var currentLen = len

        while (currentLen > 0) {
            bin[currentLen - 1] = (currentNum % 256).toByte() // Cast to Byte
            currentNum /= 256
            currentLen--
        }

        // If currentNum is not zero, return 0 (equivalent of lblKO)
        if (currentNum != 0L) {
            return 0
        }

        // Return the original length
        return len
    }

}