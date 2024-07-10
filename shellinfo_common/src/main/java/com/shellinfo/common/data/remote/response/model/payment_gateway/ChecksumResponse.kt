package com.shellinfo.common.data.remote.response.model.payment_gateway

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChecksumResponse(

    @Json(name ="s") var s: String,
    @Json(name ="dtls") var dtls: ChecksumDetail,
)


@JsonClass(generateAdapter = true)
data class ChecksumDetail(

    @Json(name ="checksum") var checksum: String,
)