package com.example.puffer.parkingdemo.parkInfo;

import com.example.puffer.parkingdemo.model.DataManager;
import com.example.puffer.parkingdemo.model.History;
import com.example.puffer.parkingdemo.model.Love;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ParkInfoPresenter implements ParkInfoContract.Presenter {
    private ParkInfoContract.View view;

    ParkInfoPresenter(ParkInfoContract.View view) {
        this.view = view;
    }

    @Override
    public void readParkData(String id) {
        DataManager.getInstance().getParkDao().getByID(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view.showParkInfo());
    }

    @Override
    public void readLoveData(String id) {
        DataManager.getInstance().getLoveDao().getByID(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view.showLove());
    }

    @Override
    public void writeHistory(String id) {
        DataManager.getInstance().getHistoryDao().insert(new History(id, System.currentTimeMillis()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @Override
    public void writeLove(String id, boolean isLove) {
        if(isLove) {
            DataManager.getInstance().getLoveDao().insert(new Love(id))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view.changeLove(true));
        } else {
            DataManager.getInstance().getLoveDao().deleteByID(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view.changeLove(false));
        }
    }
}
