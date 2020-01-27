package com.example.puffer.parkingdemo.parkList;

import com.example.puffer.parkingdemo.model.Park;

import io.reactivex.observers.DisposableSingleObserver;

interface ParkListContract {
    interface View {
        DisposableSingleObserver<Park[]> showParkList();
    }

    interface Presenter {
        void readParksData();

        void removeParkData(String id);

        void insertParkData(String id);
    }
}
