package com.example.puffer.parkingdemo.main;

import com.example.puffer.parkingdemo.model.data.Park;

import io.reactivex.observers.DisposableSingleObserver;

interface MainContract {
    interface View {
        void transitionToUpdate();

        DisposableSingleObserver<Park[]> showDataSetInfo();
    }

    interface Presenter {
        void readDataSet();

        void downloadDataSet();
    }
}
