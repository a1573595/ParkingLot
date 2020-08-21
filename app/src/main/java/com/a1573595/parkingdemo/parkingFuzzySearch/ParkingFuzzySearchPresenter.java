package com.a1573595.parkingdemo.parkingFuzzySearch;

import androidx.sqlite.db.SimpleSQLiteQuery;

import com.a1573595.parkingdemo.BasePresenter;
import com.a1573595.parkingdemo.model.DataManager;
import com.a1573595.parkingdemo.model.data.Parking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ParkingFuzzySearchPresenter extends BasePresenter<ParkingFuzzySearchView> {
    private int mode = 1;

    private ParkingFuzzySearchAdapter adapter;
    private List<Parking> parkingList = new ArrayList<>();

    public void setAdapter(ParkingFuzzySearchAdapter adapter) {
        this.adapter = adapter;
    }

    public void readParksData(String search) {
        String query = "SELECT * FROM Table_Parking WHERE ";

        switch (mode) {
            case 0:
                query += "totalbus > 0";
                break;
            case 1:
                query += "totalcar > 0";
                break;
            case 2:
                query += "totalmotor > 0";
                break;
            default:
                query += "totalbike > 0";
                break;
        }

        if (search.length() > 0) {
            query += String.format(" AND (name LIKE \'%%%s%%\' OR address LIKE \'%%%s%%\')", search, search);
        }

        addDisposable(DataManager.getInstance().getParkDao().getAllByQuery(new SimpleSQLiteQuery(query))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(showParkList()));
    }

    public int getItemCount() {
        return parkingList.size();
    }

    public Parking getItem(int position) {
        return parkingList.get(position);
    }

    public void onItemClick(int position) {
        view.onItemClick(parkingList.get(position).id);
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    private DisposableSingleObserver<Parking[]> showParkList() {
        return new DisposableSingleObserver<Parking[]>() {
            @Override
            public void onSuccess(Parking[] parkings) {
                parkingList.clear();
                Collections.addAll(parkingList, parkings);

                adapter.notifyDataSetChanged();
                view.showLayoutAnimation();
            }

            @Override
            public void onError(Throwable e) {
            }
        };
    }
}
