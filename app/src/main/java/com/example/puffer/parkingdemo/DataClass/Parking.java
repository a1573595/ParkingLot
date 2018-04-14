package com.example.puffer.parkingdemo.DataClass;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by user on 2017/12/2.
 */

public class Parking implements ClusterItem {
    public String name, area;
    public int totalCar, totalMotor, totalBike;
    private final LatLng mPosition;

    public Parking(LatLng position, String name, String area,int totalCar, int totalMotor, int totalBike) {
        mPosition = position;
        this.name = name;
        this.area = area;
        this.totalCar = totalCar;
        this.totalMotor = totalMotor;
        this.totalBike = totalBike;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return null;
    }
}
