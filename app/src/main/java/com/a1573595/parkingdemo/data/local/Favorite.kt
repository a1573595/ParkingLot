package com.a1573595.parkingdemo.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.a1573595.parkingdemo.domain.model.ParkingLot

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ParkingLot::class,
            parentColumns = ["id"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class Favorite(
    @PrimaryKey
    val id: String,
)