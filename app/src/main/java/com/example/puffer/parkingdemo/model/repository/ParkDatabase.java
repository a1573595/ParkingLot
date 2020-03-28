package com.example.puffer.parkingdemo.model.repository;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.puffer.parkingdemo.model.data.History;
import com.example.puffer.parkingdemo.model.data.Love;
import com.example.puffer.parkingdemo.model.data.Park;

@Database(entities = {Park.class, Love.class, History.class}, version = DatabaseInfo.DB_Version)
public abstract class ParkDatabase extends RoomDatabase {
    public abstract ParkDao getParkingDao();
    public abstract LoveDao getLoveDao();
    public abstract HistoryDao getHistoryDao();
}
