package com.example.puffer.parkingdemo.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(History item);

    @Query("SELECT Table_Parking.* FROM Table_History INNER JOIN Table_Parking " +
            "ON Table_History.id = Table_Parking.id ORDER BY Table_History.hashTag DESC")
    Park[] getHistoryList();

    @Query("DELETE FROM Table_History WHERE id LIKE :id")
    int deleteByID(String id);
}
