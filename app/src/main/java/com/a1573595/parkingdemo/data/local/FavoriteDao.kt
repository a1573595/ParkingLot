package com.a1573595.parkingdemo.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.a1573595.parkingdemo.domain.model.ParkingLot
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM Favorite INNER JOIN ParkingLot ON Favorite.id = ParkingLot.id Order By Favorite.rowid DESC")
    fun getParkingLotListFlow(): Flow<List<ParkingLot>>

    @Query("SELECT * FROM Favorite WHERE id LIKE :id")
    fun getFavoriteByIdFlow(id: String): Flow<Favorite?>

    @Upsert
    suspend fun upsert(favorite: Favorite)

    @Delete
    suspend fun delete(favorite: Favorite)
}