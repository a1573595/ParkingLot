package com.example.puffer.parkingdemo.parkList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.puffer.parkingdemo.R;
import com.example.puffer.parkingdemo.model.Park;

import java.util.ArrayList;

public class ParkListAdapter extends RecyclerView.Adapter<ParkListAdapter.ViewHolder> {
    private ParkListAdapterPresenter presenter;

    class ViewHolder extends RecyclerView.ViewHolder {
        View root;
        TextView tv_name, tv_address, tv_total;

        ViewHolder(View v) {
            super(v);
            root = v.findViewById(R.id.root);
            tv_name = v.findViewById(R.id.tv_name);
            tv_address = v.findViewById(R.id.tv_address);
            tv_total = v.findViewById(R.id.tv_total);
        }
    }

    public ParkListAdapter(ParkListAdapterPresenter presenter) {
        this.presenter = presenter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_park_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        presenter.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return presenter.getItemCount();
    }
}
