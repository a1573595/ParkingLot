package com.example.puffer.parkingdemo.parkList;

import com.example.puffer.parkingdemo.model.Park;

import io.reactivex.SingleObserver;

interface ParkListContract {
    interface View {
        SingleObserver<Park[]> showParkList();
    }

    interface Presenter {
        void readParksData();

        void removeParkData(String id);
    }
}
