package com.example.puffer.parkingdemo.model.repository;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Transaction;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.example.puffer.parkingdemo.model.data.Park;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface ParkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Park item);

    @Query("SELECT * FROM Table_Parking WHERE id LIKE :id")
    Single<Park> getByID(String id);

    @Query("DELETE FROM Table_Parking WHERE id LIKE :id")
    Completable deleteByID(String id);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long[]> insertAll(List<Park> parks);

    @Query("SELECT * FROM Table_Parking")
    Single<Park[]> getAll();

    @Query("SELECT * FROM Table_Parking WHERE name LIKE '%' || :name || '%'")
    Single<Park[]> getAllByName(String name);

    @RawQuery()
    Single<Park[]> getAllByQuery(SupportSQLiteQuery query);

    @Query("DELETE FROM Table_Parking")
    Completable deleteAll();
}