package com.a1573595.parkinglotdemo.database

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = TABLE_PARKING_LOT)
data class ParkingLot(
    @PrimaryKey
    val id: String,
    var area: String?,
    var name: String?,
    var summary: String?,
    var address: String?,
    var tel: String?,
    var payex: String?,
    var totalcar: Int = 0,
    var totalmotor: Int = 0,
    var totalbike: Int = 0,
    var totalbus: Int = 0,
    var lat: Double = 0.0,
    var lng: Double = 0.0,
)

class ParkingLotCallback : DiffUtil.ItemCallback<ParkingLot>() {
    override fun areItemsTheSame(oldItem: ParkingLot, newItem: ParkingLot): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ParkingLot, newItem: ParkingLot): Boolean {
        return oldItem == newItem
    }
}