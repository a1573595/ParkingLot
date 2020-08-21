package com.a1573595.parkingdemo.parkingInfo;

import com.a1573595.parkingdemo.BaseView;
import com.a1573595.parkingdemo.model.data.Love;
import com.a1573595.parkingdemo.model.data.Parking;

import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;

public interface ParkingInfoView extends BaseView {
    DisposableSingleObserver<Parking> showParkInfo();

    DisposableSingleObserver<Love> showLove();

    DisposableCompletableObserver changeLove(boolean isLove);
}
