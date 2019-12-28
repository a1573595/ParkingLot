package com.example.puffer.parkingdemo.parkList;

import com.example.puffer.parkingdemo.BasePresenter;
import com.example.puffer.parkingdemo.model.DataManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class ParkListPresenter extends BasePresenter implements ParkListContract.Presenter {
    private ParkListContract.View view;
    private boolean isLove;

    ParkListPresenter(ParkListContract.View view, boolean isLove) {
        this.view = view;
        this.isLove = isLove;
    }

    @Override
    public void readParksData() {
        if(isLove) {
            addDisposable(DataManager.getInstance().getLoveDao().getLoveList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(view.showParkList()));
        } else {
            addDisposable(DataManager.getInstance().getHistoryDao().getHistoryList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(view.showParkList()));
        }
    }

    @Override
    public void removeParkData(String id) {
        if(isLove) {
            DataManager.getInstance().getLoveDao().deleteByID(id)
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        }else {
            DataManager.getInstance().getHistoryDao().deleteByID(id)
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        }
    }
}
