package com.example.puffer.parkingdemo.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = DatabaseInfo.Table_HISTORY)
public class History {
    @PrimaryKey
    @NonNull
    String id;
    long hashTag = System.currentTimeMillis();

    public History(String id) {
        this.id = id;
    }
}
