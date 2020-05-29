package com.a1573595.parkingdemo.model.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.a1573595.parkingdemo.model.repository.DatabaseInfo;

@Entity(tableName = DatabaseInfo.Table_LOVE)
public class Love {
    @PrimaryKey
    @NonNull
    public String id;

    public Love(String id) {
        this.id = id;
    }
}
