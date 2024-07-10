package com.shellinfo.common.data.local.prefs

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.shellinfo.common.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPreferenceUtil  @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val sharedPreferences = context.getSharedPreferences(
        Constants.SHARED_PREF_FILE_NAME,
        Context.MODE_PRIVATE,
    )

//    // Initialize MasterKey and EncryptedSharedPreferences
//    private val masterKey =
//        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
//
//    val encryptedSharedPreferences = EncryptedSharedPreferences.create(
//        context,
//        Constants.ENCRYPTED_SHARED_PREF_FILE_NAME,
//        masterKey,
//        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
//    )

    fun <T> getPreference(key: String, defaultValue: T): T {
        return when (defaultValue) {
            is String -> sharedPreferences.getString(key, defaultValue) as T
            is Int -> sharedPreferences.getInt(key, defaultValue) as T
            is Float -> sharedPreferences.getFloat(key, defaultValue) as T
            is Long -> sharedPreferences.getLong(key, defaultValue) as T
            is Boolean -> sharedPreferences.getBoolean(key, defaultValue) as T
            else -> throw IllegalArgumentException("Unsupported type")
        }
    }

    fun <T> savePreference(key: String, value: T) {
        with(sharedPreferences.edit()) {
            when (value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Float -> putFloat(key, value)
                is Long -> putLong(key, value)
                is Boolean -> putBoolean(key, value)
                else -> throw IllegalArgumentException("Unsupported type")
            }
            apply()
        }
    }

    fun removePreference(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    fun clearAllPreferences() {
        sharedPreferences.edit().clear().apply()
    }

//    fun <T> getSecuredPreference(key: String, defaultValue: T): T {
//        return when (defaultValue) {
//            is String -> encryptedSharedPreferences.getString(key, defaultValue) as T
//            is Int -> encryptedSharedPreferences.getInt(key, defaultValue) as T
//            is Float -> encryptedSharedPreferences.getFloat(key, defaultValue) as T
//            is Long -> encryptedSharedPreferences.getLong(key, defaultValue) as T
//            is Boolean -> encryptedSharedPreferences.getBoolean(key, defaultValue) as T
//            else -> throw IllegalArgumentException("Unsupported type")
//        }
//    }
//
//    fun <T> saveSecuredPreference(key: String, value: T) {
//        with(encryptedSharedPreferences.edit()) {
//            when (value) {
//                is String -> putString(key, value)
//                is Int -> putInt(key, value)
//                is Float -> putFloat(key, value)
//                is Long -> putLong(key, value)
//                is Boolean -> putBoolean(key, value)
//                else -> throw IllegalArgumentException("Unsupported type")
//            }
//            apply()
//        }
//    }
}