package com.a1573595.parkingdemo.parkList;

import com.a1573595.parkingdemo.BasePresenter;
import com.a1573595.parkingdemo.model.DataManager;
import com.a1573595.parkingdemo.model.data.History;
import com.a1573595.parkingdemo.model.data.Love;
import com.a1573595.parkingdemo.model.data.Park;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ParkListPresenter extends BasePresenter implements ParkListContract.Presenter {
    private ParkListContract.View view;
    private boolean isLove;

    private ParkListAdapter adapter;
    private List<Park> parkList = new ArrayList<>();
    private Park lastDeleteItem;
    private int lastDeletePosition;

    void setView(ParkListContract.View view) {
        this.view = view;
    }

    public void setLove(boolean isLove) {
        this.isLove = isLove;
    }

    @Override
    public void setAdapter(ParkListAdapter adapter) {
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
        return parkList.size();
    }

    @Override
    public Park getItem(int position) {
        return parkList.get(position);
    }

    @Override
    public void onItemClick(int position) {
        view.onItemClick(parkList.get(position).id);
    }

    @Override
    public void removeItem(int position) {
        lastDeleteItem = parkList.get(position);
        if (isLove) {
            lastDeletePosition = position;
        } else {
            lastDeletePosition = 0;
        }

        parkList.remove(position);
        removeParkData(lastDeleteItem.id);

        adapter.notifyItemRemoved(position);
    }

    @Override
    public void undoDelete() {
        parkList.add(lastDeletePosition, lastDeleteItem);
        insertParkData(lastDeleteItem.id);

        adapter.notifyItemInserted(lastDeletePosition);
    }

    private DisposableSingleObserver<Park[]> showParkList() {
        return new DisposableSingleObserver<Park[]>() {
            @Override
            public void onSuccess(Park[] parks) {
                parkList.clear();
                Collections.addAll(parkList, parks);
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
