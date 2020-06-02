package com.a1573595.parkingdemo.parkFuzzySearch;

import com.a1573595.parkingdemo.model.data.Park;

interface ParkFuzzySearchContract {
    interface View {
        void showLayoutAnimation();

        void onItemClick(String id);
    }

    interface Presenter {
        void setAdapter(ParkFuzzySearchAdapter adapter);

        void readParksData(String search);

        int getItemCount();

        Park getItem(int position);

        void onItemClick(int position);

        void setMode(int mode);
    }
}
