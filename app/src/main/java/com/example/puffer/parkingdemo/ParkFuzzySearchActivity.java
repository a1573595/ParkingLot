package com.example.puffer.parkingdemo;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.sqlite.db.SimpleSQLiteQuery;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.puffer.parkingdemo.model.DataManager;
import com.example.puffer.parkingdemo.model.Park;
import com.example.puffer.parkingdemo.parkInfo.ParkInfoActivity;

import java.util.ArrayList;
import java.util.Collections;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ParkFuzzySearchActivity extends AppCompatActivity {
    private EditText ed_search;
    private RadioGroup transportation;
    private RecyclerView recyclerView;

    private ArrayList<Park> parkArrayList = new ArrayList<>();
    private ParkListAdapter adapter;

    private int mode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_fuzzy_search);

        findView();
        initList();
        readDataSet();
        setListen();
    }

    private void findView(){
        ed_search = findViewById(R.id.ed_search);

        transportation = findViewById(R.id.transportation);

        recyclerView = findViewById(R.id.recyclerView);
    }

    private void initList(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ParkListAdapter(parkArrayList);
        recyclerView.setAdapter(adapter);

        LayoutAnimationController controller = new LayoutAnimationController(
                AnimationUtils.loadAnimation(this, R.anim.grow_fade_in_from_bottom));
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        controller.setDelay(0.3f);
        recyclerView.setLayoutAnimation(controller);
    }

    private void readDataSet() {
        String query = "SELECT * FROM Table_Parking WHERE ";

        switch (mode) {
            case 0 :
                query += "totalbus > 0";
                break;
            case 1 :
                query += "totalcar > 0";
                break;
            case 2 :
                query += "totalmotor > 0";
                break;
            default :
                query += "totalbike > 0";
                break;
        }

        if(ed_search.getText().length() > 0) {
            query += String.format(" AND name LIKE \'%%%s%%\'", ed_search.getText().toString());
        }
        DataManager.getInstance().getParkDao().getAllByQuery(new SimpleSQLiteQuery(query))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Park[]>() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onSuccess(Park[] parks) {
                        parkArrayList.clear();
                        Collections.addAll(parkArrayList, parks);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) { }
                });
    }

    private void setListen(){
        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                readDataSet();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        transportation.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.tv_bus:
                    mode = 0;
                    break;
                case R.id.tv_car:
                    mode = 1;
                    break;
                case R.id.tv_moto:
                    mode = 2;
                    break;
                case R.id.tv_bike:
                    mode = 3;
                    break;
            }

            readDataSet();
        });

        adapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(this, ParkInfoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", parkArrayList.get(position).id);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }
}
