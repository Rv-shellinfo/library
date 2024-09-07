package com.shellinfo.common.data.local.data.ipc

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class BF200Data(
    @Json(name = "B") val b: B
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class B(
    @Json(name = "CI") val ci: CI,
    @Json(name = "TI") val ti: TI
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class CI(
    @Json(name = "57") val `57`: String?,
    @Json(name = "82") val `82`: String?,
    @Json(name = "84") val `84`: String?,
    @Json(name = "5A") val `5A`: String?,
    @Json(name = "5F24") val `5F24`: String?,
    @Json(name = "5F2A") val `5F2A`: String?,
    @Json(name = "5F28") val `5F28`: String?,
    @Json(name = "9F26") val `9F26`: String?,
    @Json(name = "4F") val `4F`: String?,
    @Json(name = "9F36") val `9F36`: String?,
    @Json(name = "5F34") val `5F34`: String?,
    @Json(name = "9F27") val `9F27`: String?,
    @Json(name = "9F6E") val `9F6E`: String?,
    @Json(name = "9F10") val `9F10`: String?,
    @Json(name = "5F25") val `5F25`: String?,
    @Json(name = "9F07") val `9F07`: String?,
    @Json(name = "9F08") val `9F08`: String?,
    @Json(name = "9F0D") val `9F0D`: String?,
    @Json(name = "9F0E") val `9F0E`: String?,
    @Json(name = "9F0F") val `9F0F`: String?,
    @Json(name = "9F5A") val `9F5A`: String?,
    @Json(name = "9F7C") val `9F7C`: String?,
    @Json(name = "T2L") val t2l: String?,
    @Json(name = "8E") val `8E`: String?,
    @Json(name = "9F19") val `9F19`: String?,
    @Json(name = "9F24") val `9F24`: String?,
    @Json(name = "9F25") val `9F25`: String?,
    @Json(name = "9F41") val `9F41`: String?,
    @Json(name = "9F63") val `9F63`: String?,
    @Json(name = "CardType") val cardType: String?,
    @Json(name = "DF33") val df33: String?
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class TI(
    @Json(name = "95") val `95`: String?,
    @Json(name = "9F4E") val `9F4E`: String?,
    @Json(name = "9A") val `9A`: String?,
    @Json(name = "9F21") val `9F21`: String?,
    @Json(name = "9C") val `9C`: String?,
    @Json(name = "9F1A") val `9F1A`: String?,
    @Json(name = "9F37") val `9F37`: String?,
    @Json(name = "9F02") val `9F02`: String?,
    @Json(name = "9F03") val `9F03`: String?,
    @Json(name = "9F09") val `9F09`: String?,
    @Json(name = "9F1E") val `9F1E`: String?,
    @Json(name = "9F33") val `9F33`: String?,
    @Json(name = "9F34") val `9F34`: String?,
    @Json(name = "9F35") val `9F35`: String?,
    @Json(name = "DF7F") val df7f: String?
): Parcelable
