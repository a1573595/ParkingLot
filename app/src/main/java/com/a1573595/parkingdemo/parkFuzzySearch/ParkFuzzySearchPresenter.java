package com.a1573595.parkingdemo.parkFuzzySearch;

import androidx.sqlite.db.SimpleSQLiteQuery;

import com.a1573595.parkingdemo.BasePresenter;
import com.a1573595.parkingdemo.model.DataManager;
import com.a1573595.parkingdemo.model.data.Park;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ParkFuzzySearchPresenter extends BasePresenter implements ParkFuzzySearchContract.Presenter {
    private ParkFuzzySearchContract.View view;
    private int mode = 1;

    private ParkFuzzySearchAdapter adapter;
    private List<Park> parkList = new ArrayList<>();

    void setView(ParkFuzzySearchContract.View view) {
        this.view = view;
    }

    @Override
    public void setAdapter(ParkFuzzySearchAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
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

    @Override
    public int getItemCount() {
        return parkList.size();
    }

    @Override
    public Park getItem(int position) {
        return parkList.get(position);
    }

    @Override
    public void onItemClick(int position) {
        view.onItemClick(parkList.get(position).id);
    }

    @Override
    public void setMode(int mode) {
        this.mode = mode;
    }

    private DisposableSingleObserver<Park[]> showParkList() {
        return new DisposableSingleObserver<Park[]>() {
            @Override
            public void onSuccess(Park[] parks) {
                parkList.clear();
                Collections.addAll(parkList, parks);

                adapter.notifyDataSetChanged();
                view.showLayoutAnimation();
            }

            @Override
            public void onError(Throwable e) {
            }
        };
    }
}
