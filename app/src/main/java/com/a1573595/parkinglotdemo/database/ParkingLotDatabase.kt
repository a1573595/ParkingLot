package com.a1573595.parkinglotdemo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.sqlcipher.database.SupportFactory

private const val DB_NAME = "PK.db"
private const val DB_VERSION = 1

const val TABLE_PARKING_LOT = "Table_Parking_Lot"
const val TABLE_FAVORITE = "Table_Favorite"
const val TABLE_HISTORY = "Table_History"

@Database(
    entities = [ParkingLot::class, Favorite::class, History::class],
    version = DB_VERSION,
    exportSchema = false
)
abstract class ParkingLotDatabase : RoomDatabase() {
    companion object {
        lateinit var instance: ParkingLotDatabase

        @Synchronized
        fun build(context: Context, password: String) {
            instance = Room.databaseBuilder(
                context,
                ParkingLotDatabase::class.java,
                DB_NAME
            )
                .openHelperFactory(SupportFactory(password.toByteArray()))
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract fun getParkingDao(): ParkingLotDao

    abstract fun getFavoriteDao(): FavoriteDao

    abstract fun getHistoryDao(): HistoryDao
}