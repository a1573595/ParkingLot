package com.example.puffer.parkingdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ParkingListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private ArrayList<String> parkings;
    private ViewHolder holder;

    private static class ViewHolder {
        TextView tv_name, tv_address, tv_total;

        ViewHolder(View v) {
            tv_name = v.findViewById(R.id.tv_name);
            tv_address = v.findViewById(R.id.tv_address);
            tv_total = v.findViewById(R.id.tv_total);
        }
    }

    public ParkingListAdapter(Context context, ArrayList<String> parkings) {
        super(context, R.layout.parking_list,parkings);
        this.context = context;
        this.parkings = parkings;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.parking_list, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        setText(position);

        return convertView;
    }

    private void setText(int position){
        String[] text = parkings.get(position).split(",");
        holder.tv_name.setText(text[0]);
        holder.tv_address.setText(text[1]);
        holder.tv_total.setText(String.format("轎車:%s / 機車:%s / 自行車:%s",
                text[2],text[3],text[4]));
    }
}
