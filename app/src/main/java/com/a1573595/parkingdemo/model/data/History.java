package com.a1573595.parkingdemo.model.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.a1573595.parkingdemo.model.repository.DatabaseInfo;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = DatabaseInfo.Table_HISTORY)
public class History {
    @PrimaryKey
    @NonNull
    public String id;
    public long hashTag = System.currentTimeMillis();

    public History(@NotNull String id) {
        this.id = id;
    }
}
