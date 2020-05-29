package com.a1573595.parkingdemo.parkFuzzySearch;

import androidx.sqlite.db.SimpleSQLiteQuery;

import com.a1573595.parkingdemo.BasePresenter;
import com.a1573595.parkingdemo.model.DataManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ParkFuzzySearchPresenter extends BasePresenter implements ParkFuzzySearchContract.Presenter {
    private ParkFuzzySearchContract.View view;
    private int mode = 1;

    ParkFuzzySearchPresenter(ParkFuzzySearchContract.View view) {
        this.view = view;
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
            query += String.format(" AND name LIKE \'%%%s%%\'", search);
        }

        addDisposable(DataManager.getInstance().getParkDao().getAllByQuery(new SimpleSQLiteQuery(query))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(view.showParkList()));
    }

    @Override
    public void setMode(int mode) {
        this.mode = mode;
    }
}
