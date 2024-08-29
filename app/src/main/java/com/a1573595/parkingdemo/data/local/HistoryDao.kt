package com.a1573595.parkingdemo.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.a1573595.parkingdemo.domain.model.ParkingLot
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM History INNER JOIN ParkingLot ON History.id = ParkingLot.id Order By History.rowid DESC")
    fun getParkingLotListFlow(): Flow<List<ParkingLot>>

    @Upsert
    suspend fun upsert(history: History)

    @Delete
    suspend fun delete(history: History)

    @Transaction
    suspend fun deleteAndUpsert(history: History) {
        delete(history)
        upsert(history)
    }
}