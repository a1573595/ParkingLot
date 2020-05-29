package com.a1573595.parkingdemo.model.repository;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.a1573595.parkingdemo.model.data.Love;
import com.a1573595.parkingdemo.model.data.Park;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface LoveDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Love item);

    @Query("SELECT * FROM Table_Love WHERE id LIKE :id")
    Single<Love> getByID(String id);

    @Query("SELECT * FROM Table_Love INNER JOIN Table_Parking ON Table_Love.id = Table_Parking.id")
    Single<Park[]> getLoveList();

    @Query("DELETE FROM Table_Love WHERE id LIKE :id")
    Completable deleteByID(String id);
}
