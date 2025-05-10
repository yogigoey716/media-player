package com.yogigoey.mediaplayer.core.network

import com.google.gson.annotations.SerializedName

open class BaseResponse {
    @SerializedName("code")
    val code: Int? = null
    @SerializedName("message")
    val message: String? = null
}