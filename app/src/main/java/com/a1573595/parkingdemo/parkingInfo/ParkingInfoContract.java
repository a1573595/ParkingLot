package com.a1573595.parkingdemo.parkingInfo;

import com.a1573595.parkingdemo.model.data.Love;
import com.a1573595.parkingdemo.model.data.Parking;

import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;

interface ParkingInfoContract {
    interface View {
        DisposableSingleObserver<Parking> showParkInfo();

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
