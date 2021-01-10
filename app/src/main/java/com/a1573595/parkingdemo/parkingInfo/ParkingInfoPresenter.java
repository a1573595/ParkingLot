package com.a1573595.parkingdemo.parkingInfo;

import com.a1573595.parkingdemo.BasePresenter;
import com.a1573595.parkingdemo.model.DataManager;
import com.a1573595.parkingdemo.model.data.History;
import com.a1573595.parkingdemo.model.data.Love;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ParkingInfoPresenter extends BasePresenter<ParkingInfoView> {
    private String id;

    void setID(String id) {
        this.id = id;
    }

    public void readParkData() {
        addDisposable(DataManager.getInstance().getParkDao().getByID(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(view.showParkInfo()));
    }

    public void readLoveData() {
        addDisposable(DataManager.getInstance().getLoveDao().getByID(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(view.showLove()));
    }

    public void addHistory() {
        addDisposable(DataManager.getInstance().getHistoryDao().insert(new History(id))
                .subscribeOn(Schedulers.io())
                .subscribe());
    }

    public void writeLove(boolean isLove) {
        if (isLove) {
            addDisposable(DataManager.getInstance().getLoveDao().insert(new Love(id))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(view.changeLove(true)));
        } else {
            addDisposable(DataManager.getInstance().getLoveDao().deleteByID(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(view.changeLove(false)));
        }
    }
}