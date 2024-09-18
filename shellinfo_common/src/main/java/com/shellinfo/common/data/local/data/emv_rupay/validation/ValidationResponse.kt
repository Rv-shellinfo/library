package com.shellinfo.common.data.local.data.emv_rupay.validation

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ValidationResponse(
    var updatedServiceData:MutableList<Byte>?=null,
)