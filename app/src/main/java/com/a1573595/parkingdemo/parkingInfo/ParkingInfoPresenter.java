package com.a1573595.parkingdemo.parkingInfo;

import com.a1573595.parkingdemo.BasePresenter;
import com.a1573595.parkingdemo.model.DataManager;
import com.a1573595.parkingdemo.model.data.History;
import com.a1573595.parkingdemo.model.data.Love;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ParkingInfoPresenter extends BasePresenter implements ParkingInfoContract.Presenter {
    private ParkingInfoContract.View view;
    private String id;

    void setView(ParkingInfoContract.View view) {
        this.view = view;
    }

    void setID(String id) {
        this.id = id;
    }

    @Override
    public void readParkData() {
        addDisposable(DataManager.getInstance().getParkDao().getByID(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(view.showParkInfo()));
    }

    @Override
    public void readLoveData() {
        addDisposable(DataManager.getInstance().getLoveDao().getByID(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(view.showLove()));
    }

    @Override
    public void addHistory() {
        addDisposable(DataManager.getInstance().getHistoryDao().insert(new History(id))
                .subscribeOn(Schedulers.io())
                .subscribe());
    }

    @Override
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
