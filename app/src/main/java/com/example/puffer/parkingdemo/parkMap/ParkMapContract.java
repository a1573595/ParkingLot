package com.example.puffer.parkingdemo.parkMap;

import com.example.puffer.parkingdemo.model.data.Park;

import io.reactivex.SingleObserver;

interface ParkMapContract {
    interface View {
        SingleObserver<Park[]> showParkMark();
    }

    interface Presenter {
        void readDataSet();
    }
}
