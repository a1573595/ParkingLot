package com.example.puffer.parkingdemo.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Park.class, Love.class, History.class}, version = DatabaseInfo.DB_Version)
abstract class ParkDatabase extends RoomDatabase {
    abstract ParkDao getParkingDao();
    abstract LoveDao getLoveDao();
    abstract HistoryDao getHistoryDao();
}
