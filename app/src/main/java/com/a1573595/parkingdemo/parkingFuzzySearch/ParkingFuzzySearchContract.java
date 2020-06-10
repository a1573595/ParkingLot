package com.a1573595.parkingdemo.parkingFuzzySearch;

import com.a1573595.parkingdemo.model.data.Parking;

interface ParkingFuzzySearchContract {
    interface View {
        void showLayoutAnimation();

        void onItemClick(String id);
    }

    interface Presenter {
        void setAdapter(ParkingFuzzySearchAdapter adapter);

        void readParksData(String search);

        int getItemCount();

        Parking getItem(int position);

        void onItemClick(int position);

        void setMode(int mode);
    }
}
