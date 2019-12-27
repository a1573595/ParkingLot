package com.example.puffer.parkingdemo.parkInfo;

import com.example.puffer.parkingdemo.model.Love;
import com.example.puffer.parkingdemo.model.Park;

import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;

interface ParkInfoContract {
    interface View {
        SingleObserver<Park> showParkInfo();

        SingleObserver<Love> showLove();

        CompletableObserver changeLove(boolean isLove);
    }

    interface Presenter {
        void readParkData(String id);

        void readLoveData(String id);

        void writeHistory(String id);

        void writeLove(String id, boolean isLove);
    }
}
