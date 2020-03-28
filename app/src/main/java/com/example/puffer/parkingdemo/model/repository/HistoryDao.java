package com.example.puffer.parkingdemo.model.repository;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.puffer.parkingdemo.model.data.History;
import com.example.puffer.parkingdemo.model.data.Park;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(History item);

    @Query("SELECT Table_Parking.* FROM Table_History INNER JOIN Table_Parking " +
            "ON Table_History.id = Table_Parking.id ORDER BY Table_History.hashTag DESC")
    Single<Park[]> getHistoryList();

    @Query("DELETE FROM Table_History WHERE id LIKE :id")
    Completable deleteByID(String id);
}
