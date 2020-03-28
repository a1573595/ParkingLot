package com.example.puffer.parkingdemo.model.repository;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreference {
    private SharedPreferences sp;

    private static final String updateTime = "updateTime";

    public SharedPreference(Context context) {
        sp = context.getSharedPreferences("UserInfo", 0);
    }

    public void clear() {
        sp.edit().clear().apply();
    }

    public void setUpdateTime(long time) {
        sp.edit().putLong(updateTime, time).apply();
    }

    public long readUpdateTime() {
        return sp.getLong(updateTime, -1);
    }
}
