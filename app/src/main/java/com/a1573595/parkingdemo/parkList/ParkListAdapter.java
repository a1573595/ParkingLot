package com.a1573595.parkingdemo.parkList;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.a1573595.parkingdemo.databinding.AdapterParkListBinding;

public class ParkListAdapter extends RecyclerView.Adapter<ParkListAdapter.ViewHolder> {
    private ParkListAdapterPresenter presenter;

    static class ViewHolder extends RecyclerView.ViewHolder {
        AdapterParkListBinding binding;

        ViewHolder(AdapterParkListBinding binding, ParkListAdapterPresenter presenter) {
            super(binding.getRoot());

            this.binding = binding;

            presenter.onClick(this);
        }
    }

    public ParkListAdapter(ParkListAdapterPresenter presenter) {
        this.presenter = presenter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterParkListBinding binding = AdapterParkListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding, presenter);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        presenter.onBindViewHolder(holder.binding, position);
    }

    @Override
    public int getItemCount() {
        return presenter.getItemCount();
    }
}
