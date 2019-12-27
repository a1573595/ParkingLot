package com.example.puffer.parkingdemo.parkList;

import com.example.puffer.parkingdemo.model.Park;

public interface ParkListAdapterContract {
    interface View {
        void onItemClick(String id);

        void notifyItemRemoved(String id);
    }

    interface Presenter {
        void loadData(Park[] parks);

        int getItemCount();

        void onBindViewHolder(ParkListAdapter.ViewHolder holder, int position);

        void removeItem(int position);
    }
}
