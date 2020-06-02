package com.a1573595.parkingdemo.parkMap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.a1573595.parkingdemo.databinding.DialogCenterLayoutBinding;

public class choiceDialogAdapter extends BaseAdapter {
    private ParkMapPresenter presenter;

    private static class ViewHolder {
        private DialogCenterLayoutBinding binding;

        ViewHolder(DialogCenterLayoutBinding binding) {
            this.binding = binding;
        }
    }

    choiceDialogAdapter(ParkMapPresenter presenter) {
        super();
        this.presenter = presenter;
    }

    @Override
    public int getCount() {
        return presenter.getCount();
    }

    @Override
    public String getItem(int position) {
        return presenter.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            DialogCenterLayoutBinding binding = DialogCenterLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            convertView = binding.getRoot();

            holder = new ViewHolder(binding);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.binding.tvItem.setText(getItem(position));

        return convertView;
    }
}
