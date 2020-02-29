package com.example.puffer.parkingdemo.parkMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.puffer.parkingdemo.R;

import java.util.ArrayList;

public class choiceDialogAdapter extends ArrayAdapter<String> {
    private final Context context;
    private ArrayList<String> string;
    private ViewHolder holder;

    private static class ViewHolder {
        private TextView tv_item;

        ViewHolder(View v) {
            tv_item = v.findViewById(R.id.tv_item);
        }
    }

    choiceDialogAdapter(Context context, ArrayList<String> string) {
        super(context, R.layout.dialog_center_layout, string);
        this.context = context;
        this.string = string;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.dialog_center_layout, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        setText(position);

        return convertView;
    }

    private void setText(int position){
        holder.tv_item.setText(string.get(position));
    }
}
