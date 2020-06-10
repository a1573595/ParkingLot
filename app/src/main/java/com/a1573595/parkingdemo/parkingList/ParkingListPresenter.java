package com.a1573595.parkingdemo.parkingList;

import com.a1573595.parkingdemo.BasePresenter;
import com.a1573595.parkingdemo.model.DataManager;
import com.a1573595.parkingdemo.model.data.History;
import com.a1573595.parkingdemo.model.data.Love;
import com.a1573595.parkingdemo.model.data.Parking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ParkingListPresenter extends BasePresenter implements ParkingListContract.Presenter {
    private ParkingListContract.View view;
    private boolean isLove;

    private ParkingListAdapter adapter;
    private List<Parking> parkingList = new ArrayList<>();
    private Parking lastDeleteItem;
    private int lastDeletePosition;

    void setView(ParkingListContract.View view) {
        this.view = view;
    }

    public void setLove(boolean isLove) {
        this.isLove = isLove;
    }

    @Override
    public void setAdapter(ParkingListAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void readParksData() {
        if (isLove) {
            addDisposable(DataManager.getInstance().getLoveDao().getLoveList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(showParkList()));
        } else {
            addDisposable(DataManager.getInstance().getHistoryDao().getHistoryList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(showParkList()));
        }
    }

    @Override
    public int getItemCount() {
        return parkingList.size();
    }

    @Override
    public Parking getItem(int position) {
        return parkingList.get(position);
    }

    @Override
    public void onItemClick(int position) {
        view.onItemClick(parkingList.get(position).id);
    }

    @Override
    public void removeItem(int position) {
        lastDeleteItem = parkingList.get(position);
        if (isLove) {
            lastDeletePosition = position;
        } else {
            lastDeletePosition = 0;
        }

        parkingList.remove(position);
        removeParkData(lastDeleteItem.id);

        adapter.notifyItemRemoved(position);
    }

    @Override
    public void undoDelete() {
        parkingList.add(lastDeletePosition, lastDeleteItem);
        insertParkData(lastDeleteItem.id);

        adapter.notifyItemInserted(lastDeletePosition);
    }

    private DisposableSingleObserver<Parking[]> showParkList() {
        return new DisposableSingleObserver<Parking[]>() {
            @Override
            public void onSuccess(Parking[] parkings) {
                parkingList.clear();
                Collections.addAll(parkingList, parkings);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
            }
        };
    }

    private void removeParkData(String id) {
        if (isLove) {
            addDisposable(DataManager.getInstance().getLoveDao().deleteByID(id)
                    .subscribeOn(Schedulers.io())
                    .subscribe());
        } else {
            addDisposable(DataManager.getInstance().getHistoryDao().deleteByID(id)
                    .subscribeOn(Schedulers.io())
                    .subscribe());
        }
    }

    private void insertParkData(String id) {
        if (isLove) {
            addDisposable(DataManager.getInstance().getLoveDao().insert(new Love(id))
                    .subscribeOn(Schedulers.io())
                    .subscribe());
        } else {
            addDisposable(DataManager.getInstance().getHistoryDao().insert(new History(id))
                    .subscribeOn(Schedulers.io())
                    .subscribe());
        }
    }
}
