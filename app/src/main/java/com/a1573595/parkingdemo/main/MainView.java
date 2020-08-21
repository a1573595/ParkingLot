package com.a1573595.parkingdemo.main;

import com.a1573595.parkingdemo.BaseView;
import com.a1573595.parkingdemo.model.data.Parking;

import io.reactivex.observers.DisposableSingleObserver;

public interface MainView extends BaseView {
    void transitionToUpdate();

    DisposableSingleObserver<Parking[]> showDataSetInfo();
}
