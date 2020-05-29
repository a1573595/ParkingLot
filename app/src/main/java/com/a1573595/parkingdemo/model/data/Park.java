package com.a1573595.parkingdemo.model.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.a1573595.parkingdemo.model.repository.DatabaseInfo;

@Entity(tableName = DatabaseInfo.Table_PARKING)
public class Park {
    @PrimaryKey
    @NonNull
    public String id;
    public String area;
    public String name;
    public String summary;
    public String address;
    public String tel;
    public String payex;
    //@ColumnInfo(name = "car")
    public int totalcar;
    //@ColumnInfo(name = "motor")
    public int totalmotor;
    //@ColumnInfo(name = "bike")
    public int totalbike;
    //@ColumnInfo(name = "bus")
    public int totalbus;
    public double lat;
    public double lng;

    public Park(String id, String area, String name, String summary, String address, String tel,
                String payex, int totalcar, int totalmotor, int totalbike, int totalbus, double lat,
                double lng) {
        this.id = id;
        this.area = area;
        this.name = name;
        this.summary = summary;
        this.address = address;
        this.tel = tel;
        this.payex = payex;
        this.totalcar = totalcar;
        this.totalmotor = totalmotor;
        this.totalbike = totalbike;
        this.totalbus = totalbus;
        this.lat = lat;
        this.lng = lng;
    }
}
