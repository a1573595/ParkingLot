package com.a1573595.parkingdemo.parkMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.a1573595.parkingdemo.R;
import com.a1573595.parkingdemo.databinding.DialogCenterLayoutBinding;

import java.util.ArrayList;

public class choiceDialogAdapter extends ArrayAdapter<String> {
    private ArrayList<String> string;
    private ViewHolder holder;

    private static class ViewHolder {
        private DialogCenterLayoutBinding binding;

        ViewHolder(DialogCenterLayoutBinding binding) {
            this.binding = binding;
        }
    }

    choiceDialogAdapter(Context context, ArrayList<String> string) {
        super(context, R.layout.dialog_center_layout, string);
        this.string = string;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            DialogCenterLayoutBinding binding = DialogCenterLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            convertView = binding.getRoot();

            holder = new ViewHolder(binding);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        setText(position);

        return convertView;
    }

    private void setText(int position) {
        holder.binding.tvItem.setText(string.get(position));
    }
}
