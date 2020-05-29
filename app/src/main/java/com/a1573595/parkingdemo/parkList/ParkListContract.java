package com.a1573595.parkingdemo.parkList;

import com.a1573595.parkingdemo.model.data.Park;

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
