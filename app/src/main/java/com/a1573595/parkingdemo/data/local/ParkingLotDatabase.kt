package com.a1573595.parkingdemo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.a1573595.parkingdemo.domain.model.ParkingLot

@Database(
    version = 1,
    entities = [
        ParkingLot::class,
        Favorite::class,
        History::class,
    ],
//    exportSchema = false,
)
abstract class ParkingLotDatabase : RoomDatabase() {
    abstract val parkingLotDao: ParkingLotDao

    abstract val favoriteDao: FavoriteDao

    abstract val historyDao: HistoryDao
}