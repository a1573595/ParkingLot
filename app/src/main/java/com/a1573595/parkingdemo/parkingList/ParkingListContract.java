package com.a1573595.parkingdemo.parkingList;

import com.a1573595.parkingdemo.model.data.Parking;

interface ParkingListContract {
    interface View {
        void onItemClick(String id);
    }

    interface Presenter {
        void setAdapter(ParkingListAdapter adapter);

        void readParksData();

        int getItemCount();

        Parking getItem(int position);

        void onItemClick(int position);

        void removeItem(int position);

        void undoDelete();
    }
}
