package com.a1573595.parkingdemo.parkingMap;

import com.a1573595.parkingdemo.model.data.Parking;

import java.util.ArrayList;

import io.reactivex.observers.DisposableSingleObserver;

interface ParkingMapContract {
    interface View {
        DisposableSingleObserver<Parking[]> showParkMark();
    }

    interface Presenter {
        void readDataSet();

        void putItems(ArrayList<String> strings);

        int getCount();

        String getItem(int position);
    }
}
