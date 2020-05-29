package com.a1573595.parkingdemo.main;

import com.a1573595.parkingdemo.model.data.Park;

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
