package com.a1573595.parkingdemo.parkList;

import com.a1573595.parkingdemo.R;
import com.a1573595.parkingdemo.databinding.AdapterParkListBinding;
import com.a1573595.parkingdemo.model.data.Park;

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
        holder.binding.root.setOnClickListener(v -> {
            final int position = holder.getAdapterPosition();

            view.onItemClick(parkList.get(position).id);
        });
    }

    @Override
    public void onBindViewHolder(AdapterParkListBinding binding, int position) {
        binding.tvName.setText(parkList.get(position).name);
        binding.tvAddress.setText(parkList.get(position).address);

        binding.tvTotal.setText(binding.tvTotal.getContext().getString(R.string.transportation,
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
