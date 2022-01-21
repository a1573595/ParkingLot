package com.a1573595.parkinglotdemo.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: History): Completable

    @Query("SELECT * FROM TABLE_HISTORY INNER JOIN TABLE_PARKING_LOT ON TABLE_HISTORY.id = TABLE_PARKING_LOT.id ORDER BY hashTag DESC")
    fun getHistoryList(): Single<List<ParkingLot>>

    @Query("DELETE FROM TABLE_HISTORY WHERE id LIKE :id")
    fun deleteByID(id: String): Completable
}