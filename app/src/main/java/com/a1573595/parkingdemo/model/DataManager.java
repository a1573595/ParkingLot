package com.a1573595.parkingdemo.model;

import android.content.Context;

import androidx.room.Room;

import com.a1573595.parkingdemo.model.repository.HistoryDao;
import com.a1573595.parkingdemo.model.repository.LoveDao;
import com.a1573595.parkingdemo.model.repository.ParkingDao;
import com.a1573595.parkingdemo.model.repository.ParkDatabase;
import com.a1573595.parkingdemo.model.repository.SharedPreference;

import net.sqlcipher.database.SupportFactory;

import static com.a1573595.parkingdemo.model.repository.DatabaseInfo.DATABASE_NAME;

public class DataManager {
    private static DataManager instance = null;

    public SharedPreference sp;
    private ParkDatabase db;

    private DataManager() {
    }

    synchronized public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }

        return instance;
    }

    public void initDatabase(Context context) {
        sp = new SharedPreference(context);

        db = Room.databaseBuilder(context, ParkDatabase.class, DATABASE_NAME)
                .openHelperFactory(new SupportFactory("userEnteredPassphrase".getBytes()))
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

    public ParkingDao getParkDao() {
        return db.getParkingDao();
    }

    public LoveDao getLoveDao() {
        return db.getLoveDao();
    }

    public HistoryDao getHistoryDao() {
        return db.getHistoryDao();
    }
}