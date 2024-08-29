package com.a1573595.parkingdemo.common

import android.os.Build
import android.util.Base64 as AndroidBase64
import java.util.Base64 as JavaBase64

object Base64EncodeDecode {
    fun String.encodeToBase64(): String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            JavaBase64.getUrlEncoder().encodeToString(this.toByteArray())
        } else {
            AndroidBase64.encodeToString(
                this.toByteArray(),
                AndroidBase64.URL_SAFE or AndroidBase64.NO_PADDING
            )
        }

    fun String.decodeFromBase64(): String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String(JavaBase64.getUrlDecoder().decode(this))
        } else {
            String(
                AndroidBase64.decode(
                    this,
                    AndroidBase64.URL_SAFE or AndroidBase64.NO_PADDING
                )
            )
        }
}