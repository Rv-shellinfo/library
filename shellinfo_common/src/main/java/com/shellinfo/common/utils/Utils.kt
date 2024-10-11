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


    /**
     * @brief bin2hex: Convert binary data (hex data) to hex string format (Example given below)
     * @param bin Input binary data as ByteArray
     * @param len Length of binary data
     * @return Hexadecimal string representation of the input binary data
     */
// ex Input : byteArrayOf(0x12, 0x34, 0x56, 0x78, 0x90, 0x12, 0x34, 0x56)
//    Output: "1234567890123456"
    fun bin2hex(bin: ByteArray, len: Int): String {
        val hexBuilder = StringBuilder(len * 2) // Each byte will be converted to two hex chars
        for (i in 0 until len) {
            val tmp = bin[i].toInt() and 0xFF  // Ensure the byte is treated as unsigned
            hexBuilder.append(nib2chr(tmp / 16)) // Convert first nibble (upper 4 bits)
            hexBuilder.append(nib2chr(tmp % 16)) // Convert second nibble (lower 4 bits)
        }
        return hexBuilder.toString()
    }

    /**
     * Converts a nibble (half-byte, i.e., 4 bits) to its corresponding hex character.
     * @param nibble The nibble value (0-15)
     * @return Hexadecimal character ('0'-'9' or 'A'-'F')
     */
    fun nib2chr(nibble: Int): Char {
        return if (nibble in 0..9) {
            (nibble + '0'.code).toChar() // Convert to '0' - '9'
        } else {
            (nibble - 10 + 'A'.code).toChar() // Convert to 'A' - 'F'
        }
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

    fun binToNum(bin: ByteArray, len: Int): Long {
        var num: Long = 0

        for (i in 0 until len) {
            // Combine the bytes back into a long
            num = (num shl 8) or (bin[i].toLong() and 0xFF)
        }

        return num
    }

}