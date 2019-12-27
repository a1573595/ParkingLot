package com.example.puffer.parkingdemo.ParkFuzzySearch;

import com.example.puffer.parkingdemo.model.Park;

import io.reactivex.SingleObserver;

interface ParkFuzzySearchContract {
    interface View {
        SingleObserver<Park[]> showParkList();
    }

    interface Presenter {
        void readParksData(String search);

        void setMode(int mode);
    }
}
