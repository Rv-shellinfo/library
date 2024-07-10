package com.shellinfo.common.utils

import android.content.Context
import android.util.Base64
import com.shellinfo.common.utils.DeviceUtils.getDeviceHashKey
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.lang.Exception
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class SecurityUtils @Inject constructor(
    @ApplicationContext private val context: Context,
){

    private var valueToEnc: String? = null
    private var dValue: String? = null
    private var eValue: String = ""
    private var encValue: ByteArray? = null
    private var decordedValue: ByteArray? = null
    private var decValue: ByteArray? = null
    private var valueToDecrypt: String? = null
    private var key: Key? = null
    private var c: Cipher? = null


    fun encrypt(value: String): String {
        eValue = value
        try {
            key = generateKey()
            c = Cipher.getInstance("AES")
            c?.init(Cipher.ENCRYPT_MODE, key)
            valueToEnc = getDeviceHashKey(context) + eValue
            encValue = c?.doFinal(valueToEnc?.toByteArray())
            eValue = Base64.encodeToString(encValue, Base64.NO_PADDING)

        } catch (e: Exception) {
            eValue = ""
            Timber.e("Error in SecurityUtil encrypt method -> ${e.message}")
        } finally {
            c = null
            valueToEnc = null
            encValue = null
            key = null
        }
        return eValue
    }

    fun decrypt(value: String?): String? {
        valueToDecrypt = value
        try {
            key = generateKey()
            c = Cipher.getInstance("AES")
            c?.init(Cipher.DECRYPT_MODE, key)

            decordedValue = Base64.decode(valueToDecrypt, Base64.NO_PADDING)
            decValue = c?.doFinal(decordedValue)
            dValue =
                decValue?.let { String(it).substring(getDeviceHashKey(context).length) }
            valueToDecrypt = dValue

        } catch (e: Exception) {
            dValue = ""
            Timber.e("Error in SecurityUtil decrypt method -> ${e.message}")
        } finally {
            c = null
            decordedValue = null
            decValue = null
            valueToDecrypt = null
            key = null
        }
        return dValue
    }

    @Throws(Exception::class)
    private fun generateKey(): Key? {
        try {
            key = SecretKeySpec(
                "MY_KEY_NEEDS_TO_PUT_HERE".toByteArray(),
                "AES",
            )
        } catch (e: Exception) {
            Timber.e("Error in SecurityUtil generateKey() method -> ${e.message}")
        }
        return key
    }
}