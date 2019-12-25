package com.example.puffer.parkingdemo.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = DatabaseInfo.Table_LOVE)
public class Love {
    @PrimaryKey
    @NonNull
    String id;

    public Love(String id){
        this.id = id;
    }
}
