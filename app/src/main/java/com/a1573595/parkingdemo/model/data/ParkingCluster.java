package com.a1573595.parkingdemo.model.data;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ParkingCluster implements ClusterItem {
    public String id, name, area;
    public int totalCar, totalMotor, totalBike, totalBus;
    private final LatLng mPosition;

    public ParkingCluster(LatLng position, String id, String name, String area, int totalCar,
                          int totalMotor, int totalBike, int totalBus) {
        mPosition = position;
        this.id = id;
        this.name = name;
        this.area = area;
        this.totalCar = totalCar;
        this.totalMotor = totalMotor;
        this.totalBike = totalBike;
        this.totalBus = totalBus;
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
