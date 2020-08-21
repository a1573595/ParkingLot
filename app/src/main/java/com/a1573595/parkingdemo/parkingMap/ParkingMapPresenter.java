package com.a1573595.parkingdemo.parkingMap;

import com.a1573595.parkingdemo.BasePresenter;
import com.a1573595.parkingdemo.model.DataManager;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ParkingMapPresenter extends BasePresenter<ParkingMapView> {
    private ArrayList<String> strings = new ArrayList<>();

    public void readDataSet() {
        addDisposable(DataManager.getInstance().getParkDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(view.showParkMark()));
    }

    public void putItems(ArrayList<String> strings) {
        this.strings.clear();
        this.strings.addAll(strings);
    }

    public int getCount() {
        return strings.size();
    }

    public String getItem(int position) {
        return strings.get(position);
    }
}
