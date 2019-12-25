package com.example.puffer.parkingdemo.model;

import android.content.Context;
import android.content.SharedPreferences;

public class sharedPreference {
    private SharedPreferences sp;

    private static final String updateTime = "updateTime";
    private static final String dataSetCount = "dataSetCount";

    sharedPreference(Context context) {
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

    public void setDatasetCount(int number) {
        sp.edit().putInt(dataSetCount, number).apply();
    }

    public int readDatasetCount() {
        return sp.getInt(dataSetCount, -1);
    }
}
