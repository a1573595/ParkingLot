package com.example.puffer.parkingdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.puffer.parkingdemo.model.DataManager;
import com.example.puffer.parkingdemo.model.Park;

import java.util.ArrayList;
import java.util.Collections;

public class ParkListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    private ArrayList<Park> parkArrayList = new ArrayList<>();
    private ParkListAdapter adapter;

    private int mode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_list);

        mode = getIntent().getIntExtra("mode",0);

        findView();
        initList();
        setListen();
    }

    @Override
    public void onStart(){
        super.onStart();

        readDataSet();
    }

    private void findView(){
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ParkListAdapter(parkArrayList);
        recyclerView.setAdapter(adapter);
    }

    private void readDataSet() {
        Park[] parks;

        if(mode == 1)
            parks = DataManager.getInstance().getHistoryDao().getHistoryList();
        else
            parks = DataManager.getInstance().getLoveDao().getLoveList();

        parkArrayList.clear();
        Collections.addAll(parkArrayList, parks);
        adapter.notifyDataSetChanged();
    }

    private void setListen(){
        adapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(this, ParkInfoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", parkArrayList.get(position).id);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }
}
