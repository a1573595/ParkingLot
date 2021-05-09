package com.a1573595.parkingdemo.parkingList;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.a1573595.parkingdemo.R;
import com.a1573595.parkingdemo.databinding.AdapterParkingListBinding;
import com.a1573595.parkingdemo.model.data.Parking;

public class ParkingListAdapter extends RecyclerView.Adapter<ParkingListAdapter.ViewHolder> {
    private final ParkingListPresenter presenter;

    static class ViewHolder extends RecyclerView.ViewHolder {
        AdapterParkingListBinding binding;

        ViewHolder(AdapterParkingListBinding binding, ParkingListPresenter presenter) {
            super(binding.getRoot());

            this.binding = binding;

            binding.root.setOnClickListener(v -> {
                final int position = getAbsoluteAdapterPosition();

                presenter.onItemClick(position);
            });
        }
    }

    public ParkingListAdapter(ParkingListPresenter presenter) {
        this.presenter = presenter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterParkingListBinding binding = AdapterParkingListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding, presenter);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Parking parking = presenter.getItem(position);

        holder.binding.tvName.setText(parking.name);
        holder.binding.tvAddress.setText(parking.address);

        holder.binding.tvTotal.setText(
                holder.binding.tvTotal.getContext().getString(R.string.transportation,
                        parking.totalbus, parking.totalcar, parking.totalmotor, parking.totalbike));
    }

    @Override
    public int getItemCount() {
        return presenter.getItemCount();
    }
}