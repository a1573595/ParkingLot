package com.a1573595.parkingdemo.common

import kotlinx.serialization.json.Json

val jsonConvert = Json {
    allowStructuredMapKeys = true
    isLenient = true
    ignoreUnknownKeys = true
}