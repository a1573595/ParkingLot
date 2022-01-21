package com.a1573595.parkinglotdemo.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: Favorite): Completable

    @Query("SELECT * FROM TABLE_FAVORITE WHERE id LIKE :id")
    fun getByID(id: String): Single<Favorite>

    @Query("SELECT * FROM TABLE_FAVORITE INNER JOIN TABLE_PARKING_LOT ON TABLE_FAVORITE.id = TABLE_PARKING_LOT.id")
    fun getLoveList(): Single<List<ParkingLot>>

    @Query("DELETE FROM TABLE_FAVORITE WHERE id LIKE :id")
    fun deleteByID(id: String): Completable
}