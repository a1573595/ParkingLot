package com.example.puffer.parkingdemo.parkInfo;

import com.example.puffer.parkingdemo.BasePresenter;
import com.example.puffer.parkingdemo.model.DataManager;
import com.example.puffer.parkingdemo.model.History;
import com.example.puffer.parkingdemo.model.Love;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class ParkInfoPresenter extends BasePresenter implements ParkInfoContract.Presenter {
    private ParkInfoContract.View view;
    private String id;

    ParkInfoPresenter(ParkInfoContract.View view, String id) {
        this.view = view;
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
        DataManager.getInstance().getHistoryDao().insert(new History(id, System.currentTimeMillis()))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    @Override
    public void writeLove(boolean isLove) {
        if(isLove) {
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
