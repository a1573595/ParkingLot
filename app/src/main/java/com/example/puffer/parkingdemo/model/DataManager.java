package com.example.puffer.parkingdemo.model;

import android.content.Context;

import androidx.room.Room;

import static com.example.puffer.parkingdemo.model.DatabaseInfo.DATABASE_NAME;

public class DataManager {
    private static DataManager instance = null;

    public sharedPreference sp;
    private ParkDatabase db;

    private DataManager(){ }

    public static DataManager getInstance() {
        if (instance == null)
            instance = new DataManager();
        return instance;
    }

    public void initDatabase(Context context) {
        sp = new sharedPreference(context);
        db = Room.databaseBuilder(context, ParkDatabase.class, DATABASE_NAME)
                //.allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    public void closeDatabase() {
        db.close();
    }

    public void cleanLocalData() {
        db.clearAllTables();
        sp.clear();
    }

    public ParkDao getParkDao() {
        return db.getParkingDao();
    }

    public LoveDao getLoveDao() {
        return db.getLoveDao();
    }

    public HistoryDao getHistoryDao() {
        return db.getHistoryDao();
    }
}
