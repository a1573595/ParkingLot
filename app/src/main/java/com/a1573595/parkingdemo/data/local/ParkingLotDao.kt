package com.a1573595.parkingdemo.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Upsert
import androidx.sqlite.db.SupportSQLiteQuery
import com.a1573595.parkingdemo.domain.model.ParkingLot
import kotlinx.coroutines.flow.Flow

@Dao
interface ParkingLotDao {
    @Query("SELECT * FROM ParkingLot Order By id")
    fun getParkingLotListFlow(): Flow<List<ParkingLot>>

    @Query("SELECT * FROM ParkingLot WHERE id=:id")
    suspend fun getParkingLotById(id: String): ParkingLot?

    @Query("SELECT * FROM ParkingLot WHERE name LIKE '%' || :keyword || '%' OR address LIKE '%' || :keyword || '%' Order By id")
    fun pagingSource(keyword: String): PagingSource<Int, ParkingLot>

    @RawQuery(observedEntities = [ParkingLot::class])
    fun pagingSource(query: SupportSQLiteQuery): PagingSource<Int, ParkingLot>

    @Transaction
    @Upsert
    suspend fun upsertAll(parkingLotList: List<ParkingLot>): List<Long>
}