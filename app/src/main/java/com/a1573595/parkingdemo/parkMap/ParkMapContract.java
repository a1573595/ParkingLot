package com.a1573595.parkingdemo.parkMap;

import com.a1573595.parkingdemo.model.data.Park;

import java.util.ArrayList;

import io.reactivex.observers.DisposableSingleObserver;

interface ParkMapContract {
    interface View {
        DisposableSingleObserver<Park[]> showParkMark();
    }

    interface Presenter {
        void readDataSet();

        void putItems(ArrayList<String> strings);

        int getCount();

        String getItem(int position);
    }
}
