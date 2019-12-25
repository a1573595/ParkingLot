package com.example.puffer.parkingdemo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.puffer.parkingdemo.model.Park;

import java.util.ArrayList;

public class ParkListAdapter extends RecyclerView.Adapter<ParkListAdapter.ViewHolder>
        implements View.OnClickListener {
    private ArrayList<Park> parkArrayList;
    private OnItemClickListener clickListener = null;

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

    ParkListAdapter(ArrayList<Park> parkArrayList) {
        this.parkArrayList = parkArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.park_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_name.setText(parkArrayList.get(position).name);
        holder.tv_address.setText(parkArrayList.get(position).address);
        holder.tv_total.setText(String.format("公車:%s / 轎車:%s / 機車:%s / 自行車:%s",
                parkArrayList.get(position).totalbus, parkArrayList.get(position).totalcar,
                parkArrayList.get(position).totalmotor, parkArrayList.get(position).totalbike));

        holder.root.setTag(position);
        holder.root.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return parkArrayList.size();
    }

    @Override
    public void onClick(View view) {
        clickListener.onItemClick((int)view.getTag());
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }
}
