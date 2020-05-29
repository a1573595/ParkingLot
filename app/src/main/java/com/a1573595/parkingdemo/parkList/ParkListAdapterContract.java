package com.a1573595.parkingdemo.parkList;

import com.a1573595.parkingdemo.databinding.AdapterParkListBinding;
import com.a1573595.parkingdemo.model.data.Park;

public interface ParkListAdapterContract {
    interface View {
        void onItemClick(String id);

        void itemRemoved(String id);

        void itemInsert(String id);
    }

    interface Presenter {
        void loadData(Park[] parks);

        int getItemCount();

        void onClick(ParkListAdapter.ViewHolder holder);

        void onBindViewHolder(AdapterParkListBinding binding, int position);

        void removeItem(int position);

        void undoDelete();
    }
}
