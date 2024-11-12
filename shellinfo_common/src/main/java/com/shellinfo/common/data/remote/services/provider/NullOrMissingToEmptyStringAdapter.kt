package com.shellinfo.common.data.remote.services.provider

import com.shellinfo.common.di.NullToDefault
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import io.reactivex.annotations.Nullable

class NullOrMissingToEmptyStringAdapter {

    @ToJson
    fun toJson(@NullToDefault value: Any?): Any? {
        return value
    }

    @FromJson
    @NullToDefault
    fun fromJson(@Nullable data: Any?): Any? {
        return data ?: getDefaultForType(data?.javaClass ?: Any::class.java)
    }

    private fun getDefaultForType(type: Class<*>): Any {
        return when (type) {
            String::class.java -> ""
            Int::class.java -> 0
            Double::class.java -> 0.0
            Boolean::class.java -> false
            else -> throw IllegalArgumentException("Unsupported type: ${type.name}")
        }
    }
}



