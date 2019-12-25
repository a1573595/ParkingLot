package com.example.puffer.parkingdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.puffer.parkingdemo.model.Park;

import java.util.ArrayList;

public class ParkListAdapter extends ArrayAdapter<Park> {
    private ArrayList<Park> parkArrayList;
    private ViewHolder holder;

    private static class ViewHolder {
        TextView tv_name, tv_address, tv_total;

        ViewHolder(View v) {
            tv_name = v.findViewById(R.id.tv_name);
            tv_address = v.findViewById(R.id.tv_address);
            tv_total = v.findViewById(R.id.tv_total);
        }
    }

    public ParkListAdapter(Context context, ArrayList<Park> parkArrayList) {
        super(context, R.layout.park_list, parkArrayList);
        this.parkArrayList = parkArrayList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.park_list, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        setText(position);

        return convertView;
    }

    private void setText(int position){
        holder.tv_name.setText(parkArrayList.get(position).name);
        holder.tv_address.setText(parkArrayList.get(position).address);
        holder.tv_total.setText(String.format("公車:%s / 轎車:%s / 機車:%s / 自行車:%s",
                parkArrayList.get(position).totalbus, parkArrayList.get(position).totalcar,
                parkArrayList.get(position).totalmotor, parkArrayList.get(position).totalbike));
    }
}
