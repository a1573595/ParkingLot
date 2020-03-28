package com.example.puffer.parkingdemo.parkList;

import com.example.puffer.parkingdemo.R;
import com.example.puffer.parkingdemo.model.data.Park;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParkListAdapterPresenter implements ParkListAdapterContract.Presenter {
    private ParkListAdapterContract.View view;
    private List<Park> parkList = new ArrayList<>();

    private Park lastDeleteItem;
    private int lastDeletePosition;

    public ParkListAdapterPresenter(ParkListAdapterContract.View view) {
        this.view = view;
    }

    @Override
    public void loadData(Park[] parks) {
        parkList.clear();
        Collections.addAll(parkList, parks);
    }

    @Override
    public int getItemCount() {
        return parkList.size();
    }

    @Override
    public void onClick(ParkListAdapter.ViewHolder holder) {
        holder.root.setOnClickListener(v -> {
            final int position = holder.getAdapterPosition();

            view.onItemClick(parkList.get(position).id);
        });
    }

    @Override
    public void onBindViewHolder(ParkListAdapter.ViewHolder holder, int position) {
        holder.tv_name.setText(parkList.get(position).name);
        holder.tv_address.setText(parkList.get(position).address);

        holder.tv_total.setText(holder.tv_total.getContext().getString(R.string.transportation,
                parkList.get(position).totalbus, parkList.get(position).totalcar,
                parkList.get(position).totalmotor, parkList.get(position).totalbike));
    }

    @Override
    public void removeItem(int position) {
        lastDeleteItem = parkList.get(position);
        lastDeletePosition = position;

        parkList.remove(position);
        view.itemRemoved(lastDeleteItem.id);
    }

    @Override
    public void undoDelete() {
        parkList.add(lastDeletePosition, lastDeleteItem);
        view.itemInsert(lastDeleteItem.id);
    }
}
