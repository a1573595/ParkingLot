package com.example.puffer.parkingdemo.parkInfo;

import com.example.puffer.parkingdemo.model.Love;
import com.example.puffer.parkingdemo.model.Park;

import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;

interface ParkInfoContract {
    interface View {
        DisposableSingleObserver<Park> showParkInfo();

        DisposableSingleObserver<Love> showLove();

        DisposableCompletableObserver changeLove(boolean isLove);
    }

    interface Presenter {
        void readParkData();

        void readLoveData();

        void addHistory();

        void writeLove(boolean isLove);
    }
}
