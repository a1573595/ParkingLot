package com.example.puffer.parkingdemo.parkList;

import com.example.puffer.parkingdemo.model.DataManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class ParkListPresenter implements ParkListContract.Presenter {
    private ParkListContract.View view;
    private boolean isLove;

    ParkListPresenter(ParkListContract.View view, boolean isLove) {
        this.view = view;
        this.isLove = isLove;
    }

    @Override
    public void readParksData() {
        if(isLove) {
            DataManager.getInstance().getLoveDao().getLoveList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view.showParkList());
        } else {
            DataManager.getInstance().getHistoryDao().getHistoryList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view.showParkList());
        }
    }

    @Override
    public void removeParkData(String id) {
        if(isLove) {
            DataManager.getInstance().getLoveDao().deleteByID(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }else {
            DataManager.getInstance().getHistoryDao().deleteByID(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }
    }
}
