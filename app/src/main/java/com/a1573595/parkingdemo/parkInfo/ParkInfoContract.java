package com.a1573595.parkingdemo.parkInfo;

import com.a1573595.parkingdemo.model.data.Love;
import com.a1573595.parkingdemo.model.data.Park;

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
