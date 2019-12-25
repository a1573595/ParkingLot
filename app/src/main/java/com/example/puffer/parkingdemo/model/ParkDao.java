package com.example.puffer.parkingdemo.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Transaction;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

@Dao
public interface ParkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Park item);

    @Query("SELECT * FROM Table_Parking WHERE id LIKE :id")
    Park getByID(String id);

    @Query("DELETE FROM Table_Parking WHERE id LIKE :id")
    int deleteByID(String id);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long[] insertAll(List<Park> parks);

    @Query("SELECT * FROM Table_Parking")
    Park[] getAll();

    @Query("SELECT * FROM Table_Parking WHERE name LIKE '%' || :name || '%'")
    Park[] getAllByName(String name);

    @RawQuery()
    Park[] getAllByQuery(SupportSQLiteQuery query);

    @Query("DELETE FROM Table_Parking")
    void deleteAll();
}