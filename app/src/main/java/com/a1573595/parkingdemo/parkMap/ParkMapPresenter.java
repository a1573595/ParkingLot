package com.a1573595.parkingdemo.parkMap;

import com.a1573595.parkingdemo.model.DataManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class ParkMapPresenter implements ParkMapContract.Presenter {
    private ParkMapContract.View view;

    ParkMapPresenter(ParkMapContract.View view) {
        this.view = view;
    }

    @Override
    public void readDataSet() {
        DataManager.getInstance().getParkDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view.showParkMark());
    }
}
