package com.example.puffer.parkingdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mmslab on 2017/12/20.
 */

public class choiceDialogAdapter extends ArrayAdapter<String> {

    private final Context context;
    private ArrayList<String> string;
    private ViewHolder holder;

    static class ViewHolder {
        private TextView text;
    }

    public choiceDialogAdapter(Context context, ArrayList<String> string) {
        super(context, R.layout.dialog_center_layout, string);
        this.context = context;
        this.string = string;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.dialog_center_layout, parent, false);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        findView(convertView);
        setText(position);

        return convertView;
    }

    private void findView(View convertView){
        holder.text = (TextView) convertView.findViewById(R.id.tv_item);
    }

    private void setText(int position){
        holder.text.setText(string.get(position));
    }
}
