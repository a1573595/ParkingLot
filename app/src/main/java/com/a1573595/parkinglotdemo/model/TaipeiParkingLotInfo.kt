package com.a1573595.parkinglotdemo.model

import com.google.gson.annotations.SerializedName

class TaipeiParkingLotInfo {
    @SerializedName("data")
    val data: Data = Data()

    class Data {
        @SerializedName("park")
        val park: Array<Park> = emptyArray()

        class Park {
            @SerializedName("id")
            var id: String? = null
            @SerializedName("area")
            var area : String? = null
            @SerializedName("name")
            var name : String? = null
            @SerializedName("summary")
            var summary : String? = null
            @SerializedName("address")
            var address : String? = null
            @SerializedName("tel")
            var tel : String? = null
            @SerializedName("payex")
            var payex : String? = null
            @SerializedName("tw97x")
            var tw97x = 0.0
            @SerializedName("tw97y")
            var tw97y = 0.0
            @SerializedName("totalcar")
            var totalcar = 0
            @SerializedName("totalmotor")
            var totalmotor = 0
            @SerializedName("totalbike")
            var totalbike = 0
            @SerializedName("totalbus")
            var totalbus = 0
        }
    }
}