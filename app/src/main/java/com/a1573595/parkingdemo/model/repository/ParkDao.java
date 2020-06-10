package com.a1573595.parkingdemo.model.repository;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Transaction;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.a1573595.parkingdemo.model.data.Parking;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface ParkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Parking item);

    @Query("SELECT * FROM Table_Parking WHERE id LIKE :id")
    Single<Parking> getByID(String id);

    @Query("DELETE FROM Table_Parking WHERE id LIKE :id")
    Completable deleteByID(String id);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long[]> insertAll(List<Parking> parkings);

    @Query("SELECT * FROM Table_Parking")
    Single<Parking[]> getAll();

    @Query("SELECT * FROM Table_Parking WHERE name LIKE '%' || :name || '%'")
    Single<Parking[]> getAllByName(String name);

    @RawQuery()
    Single<Parking[]> getAllByQuery(SupportSQLiteQuery query);

    @Query("DELETE FROM Table_Parking")
    Completable deleteAll();
}