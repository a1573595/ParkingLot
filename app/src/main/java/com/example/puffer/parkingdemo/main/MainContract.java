package com.example.puffer.parkingdemo.main;

import android.content.Context;

import com.example.puffer.parkingdemo.model.Park;

import io.reactivex.SingleObserver;

interface MainContract {
    interface View {
        SingleObserver<Park[]> showDataSetInfo();
    }

    interface Presenter {
        void readDataSet();

        void downloadDataSet();
    }
}
