package com.example.puffer.parkingdemo.parkFuzzySearch;

import com.example.puffer.parkingdemo.model.Park;

import io.reactivex.observers.DisposableSingleObserver;

interface ParkFuzzySearchContract {
    interface View {
        DisposableSingleObserver<Park[]> showParkList();
    }

    interface Presenter {
        void readParksData(String search);

        void setMode(int mode);
    }
}
