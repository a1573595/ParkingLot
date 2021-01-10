package com.a1573595.parkingdemo.model.repository;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.a1573595.parkingdemo.model.data.History;
import com.a1573595.parkingdemo.model.data.Love;
import com.a1573595.parkingdemo.model.data.Parking;

@Database(entities = {Parking.class, Love.class, History.class}, version = DatabaseInfo.DB_Version)
public abstract class ParkDatabase extends RoomDatabase {
    public abstract ParkingDao getParkingDao();

    public abstract LoveDao getLoveDao();

    public abstract HistoryDao getHistoryDao();
}