package com.example.puffer.parkingdemo.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface LoveDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Love item);

    @Query("SELECT * FROM Table_Love WHERE id LIKE :id")
    Love getByID(String id);

    @Query("SELECT * FROM Table_Love INNER JOIN Table_Parking ON Table_Love.id = Table_Parking.id")
    Park[] getLoveList();

    @Query("DELETE FROM Table_Love WHERE id LIKE :id")
    int deleteByID(String id);
}
