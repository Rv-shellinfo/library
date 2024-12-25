package com.shellinfo.common.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.shellinfo.common.data.remote.response.model.ticket.Ticket
import com.squareup.moshi.Moshi
import java.lang.Exception
import javax.inject.Inject

class BarcodeUtils @Inject constructor(
    private val moshi: Moshi
){


    /**
     * method to generate qr tickets
     */
    fun generateQrCode(ticket: Ticket, width: Int? ,height: Int?):Bitmap{

        // Get JsonAdapter for your data class
        val jsonAdapter = moshi.adapter(Ticket::class.java)

        //convert to json string the qr data
        val qrData= jsonAdapter.toJson(ticket)

        try{

            //create bit matrix for bar code generation
            val bitMatrix: BitMatrix = MultiFormatWriter().encode(
                qrData,
                BarcodeFormat.QR_CODE,
                width!!, // Width of the barcode
                height!! // Height of the barcode
            )

            //return bitmap
            return toBitmap(bitMatrix)


        }catch (ex:Exception){

            //return error bitmap
            return Bitmap.createBitmap(width!!, height!!, Bitmap.Config.RGB_565)

        }

    }


    private fun toBitmap(matrix: BitMatrix): Bitmap {
        val width: Int = matrix.width
        val height: Int = matrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(x, y, if (matrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bmp
    }
}