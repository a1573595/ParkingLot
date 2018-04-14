package com.example.puffer.parkingdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.puffer.parkingdemo.DataClass.Taipei_Parking_Info;

import java.util.ArrayList;

/**
 * Created by user on 2017/12/4.
 */

public class ParkingListAdapter extends ArrayAdapter<String> {

    private final Context context;
    private ArrayList<String> parkings;
    private ViewHolder holder;

    static class ViewHolder {
        TextView name, address, total;
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
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.parking_list, parent, false);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        findView(convertView);
        setText(position);

        return convertView;
    }

    private void findView(View convertView){
        holder.name = (TextView) convertView.findViewById(R.id.name);
        holder.address = (TextView) convertView.findViewById(R.id.address );
        holder.total = (TextView) convertView.findViewById(R.id.total);
    }

    private void setText(int position){
        String[] text = parkings.get(position).split(",");
        holder.name.setText(text[0]);
        holder.address.setText(text[1]);
        holder.total.setText(String.format("轎車:%s / 機車:%s / 自行車:%s",
                text[2],text[3],text[4]));
    }
}
