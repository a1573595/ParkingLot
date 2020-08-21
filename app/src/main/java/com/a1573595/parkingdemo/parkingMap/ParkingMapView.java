package com.a1573595.parkingdemo.parkingMap;

import com.a1573595.parkingdemo.BaseView;
import com.a1573595.parkingdemo.model.data.Parking;

import io.reactivex.observers.DisposableSingleObserver;

public interface ParkingMapView extends BaseView {
    DisposableSingleObserver<Parking[]> showParkMark();
}
