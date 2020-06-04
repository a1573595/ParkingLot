package com.a1573595.parkingdemo.parkMap;

import com.a1573595.parkingdemo.BasePresenter;
import com.a1573595.parkingdemo.model.DataManager;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ParkMapPresenter extends BasePresenter implements ParkMapContract.Presenter {
    private ParkMapContract.View view;

    private ArrayList<String> strings = new ArrayList<>();

    void setView(ParkMapContract.View view) {
        this.view = view;
    }

    @Override
    public void readDataSet() {
        addDisposable(DataManager.getInstance().getParkDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(view.showParkMark()));
    }

    @Override
    public void putItems(ArrayList<String> strings) {
        this.strings.clear();
        this.strings.addAll(strings);
    }

    @Override
    public int getCount() {
        return strings.size();
    }

    @Override
    public String getItem(int position) {
        return strings.get(position);
    }
}
