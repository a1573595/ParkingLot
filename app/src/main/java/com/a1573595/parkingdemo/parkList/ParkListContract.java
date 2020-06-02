package com.a1573595.parkingdemo.parkList;

import com.a1573595.parkingdemo.model.data.Park;

interface ParkListContract {
    interface View {
        void onItemClick(String id);
    }

    interface Presenter {
        void setAdapter(ParkListAdapter adapter);

        void readParksData();

        int getItemCount();

        Park getItem(int position);

        void onItemClick(int position);

        void removeItem(int position);

        void undoDelete();
    }
}
