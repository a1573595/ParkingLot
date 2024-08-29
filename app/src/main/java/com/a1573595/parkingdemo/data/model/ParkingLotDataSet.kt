package com.a1573595.parkingdemo.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParkingLotDataSet(
    val data: Data,
)

@Serializable
data class Data(
    val park: List<Park>,
)

@Serializable
data class Park(
    val id: String?,
    val area: String?,
    val name: String?,
    val summary: String?,
    val address: String?,
    val tel: String?,
    @SerialName("payex")
    val payEx: String?,
    val tw97x: Double?,
    val tw97y: Double?,
    @SerialName("totalcar")
    val totalCar: Int?,
    @SerialName("totalmotor")
    val totalMotor: Int?,
    @SerialName("totalbike")
    val totalBike: Int?,
    @SerialName("totalbus")
    val totalBus: Int?,
)