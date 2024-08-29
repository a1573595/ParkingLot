package com.a1573595.parkingdemo.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.a1573595.parkingdemo.common.LatLngConverter
import com.a1573595.parkingdemo.data.model.Park

@Entity
data class ParkingLot(
    @PrimaryKey val id: String,
    val area: String,
    val name: String,
    val summary: String,
    val address: String,
    val tel: String,
    val payEx: String,
    val lat: Double,
    val lon: Double,
    val totalCar: Int,
    val totalMotor: Int,
    val totalBike: Int,
    val totalBus: Int,
) {
    companion object {
        fun fromPark(park: Park): ParkingLot {
            val latlonPair = LatLngConverter.twd97ToLonLat(park.tw97x!!, park.tw97y!!)
            return ParkingLot(
                id = park.id!!,
                area = park.area ?: "Unknown Area",
                name = park.name ?: "Unknown Name",
                summary = park.summary ?: "No Summary Available",
                address = park.address ?: "No Address Available",
                tel = park.tel ?: "No Tel Available",
                payEx = park.payEx ?: "No PayEx Available",
                lat = latlonPair.first,
                lon = latlonPair.second,
                totalCar = park.totalCar ?: 0,
                totalMotor = park.totalMotor ?: 0,
                totalBike = park.totalBike ?: 0,
                totalBus = park.totalBus ?: 0,
            )
        }
    }
}
