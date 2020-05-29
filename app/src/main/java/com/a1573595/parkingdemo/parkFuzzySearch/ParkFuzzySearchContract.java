package com.a1573595.parkingdemo.parkFuzzySearch;

import com.a1573595.parkingdemo.model.data.Park;

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
