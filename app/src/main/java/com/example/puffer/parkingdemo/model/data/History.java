package com.example.puffer.parkingdemo.model.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.puffer.parkingdemo.model.repository.DatabaseInfo;

@Entity(tableName = DatabaseInfo.Table_HISTORY)
public class History {
    @PrimaryKey
    @NonNull
    public String id;
    public long hashTag = System.currentTimeMillis();

    public History(String id) {
        this.id = id;
    }
}
