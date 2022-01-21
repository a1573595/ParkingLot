package com.a1573595.parkinglotdemo.database

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface ParkingLotDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: ParkingLot): Completable

    @Query("SELECT * FROM TABLE_PARKING_LOT WHERE id LIKE :id")
    fun getByID(id: String): Single<ParkingLot>

    @Query("DELETE FROM TABLE_PARKING_LOT WHERE id LIKE :id")
    fun deleteByID(id: String): Completable

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(parkingLots: List<ParkingLot>): Single<List<Long>>

    @Query("SELECT * FROM TABLE_PARKING_LOT")
    fun getAll(): Single<List<ParkingLot>>

    @Query("SELECT * FROM TABLE_PARKING_LOT WHERE name LIKE '%' || :name || '%'")
    fun getAllByName(name: String): Single<List<ParkingLot>>

    @RawQuery
    fun getAllByQuery(query: SupportSQLiteQuery): Single<List<ParkingLot>>

    @Query("DELETE FROM TABLE_PARKING_LOT")
    fun deleteAll(): Completable
}