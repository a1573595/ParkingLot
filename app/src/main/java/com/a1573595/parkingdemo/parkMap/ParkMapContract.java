package com.a1573595.parkingdemo.parkMap;

import com.a1573595.parkingdemo.model.data.Park;

import io.reactivex.SingleObserver;

interface ParkMapContract {
    interface View {
        SingleObserver<Park[]> showParkMark();
    }

    interface Presenter {
        void readDataSet();
    }
}
