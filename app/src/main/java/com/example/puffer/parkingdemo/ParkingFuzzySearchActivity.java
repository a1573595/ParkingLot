package com.example.puffer.parkingdemo;

import android.app.Activity;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;

import java.util.ArrayList;

public class ParkingFuzzySearchActivity extends AppCompatActivity {
    private Activity mActivity;

    private SQLiteDatabase draw;

    private EditText search;
    private RadioGroup transportation;
    private ListView list;

    private ArrayList<String> parkingList;
    private ParkingListAdapter parkingListAdapter;

    private int mode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_fuzzy_search);
        mActivity = this;

        initDataBase();

        findView();
        initList();
        setListen();
    }

    private void initDataBase(){
        MyDB mydb=new MyDB(this);
        draw = mydb.getWritableDatabase();
    }

    private void findView(){
        search = findViewById(R.id.ed_search);

        transportation = findViewById(R.id.transportation);

        list = findViewById(R.id.listView);
    }

    private void initList(){
        parkingList = new ArrayList<>();

        String search_text = "SELECT Name,Address,TotalCar,TotalMotor,TotalBike FROM Parking_information";
        Cursor mcursor = draw.rawQuery(search_text, null);
        mcursor.moveToFirst();

        for(int i=0;i<mcursor.getCount();i++){
            parkingList.add(mcursor.getString(0)+","+mcursor.getString(1)+"," +
                    mcursor.getString(2)+","+mcursor.getString(3)+","+mcursor.getString(4));

            mcursor.moveToNext();
        }
        mcursor.close();

        parkingListAdapter = new ParkingListAdapter(mActivity,parkingList);
        list.setAdapter(parkingListAdapter);


        //Log.e("test",String.valueOf(draw.rawQuery("Update taipei_parking_info as "+search_text, null)));
    }

    private void setListen(){
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        transportation.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.tv_car:
                    mode = 0;
                    search();
                    break;
                case R.id.tv_moto:
                    mode = 1;
                    search();
                    break;
                case R.id.tv_bike:
                    mode = 2;
                    search();
                    break;
            }
        });

        list.setOnItemClickListener((parent, view, position, id) -> {
            String[] test = parkingList.get(position).split(",");
            Intent intent = new Intent(mActivity,ParkingInfoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("name",test[0]);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
            System.gc();
        }
    }

    private void search(){
        parkingList.clear();
        String search_text;
        if(search.length()==0){
            search_text = "SELECT Name,Address,TotalCar,TotalMotor,TotalBike FROM Parking_information";
            switch (mode){
                case 0:
                    search_text += " WHERE TotalCar > 0";
                    break;
                case 1:
                    search_text += " WHERE TotalMotor > 0";
                    break;
                case 2:
                    search_text += " WHERE TotalBike > 0";
                    break;
            }
        }else{
            search_text = "SELECT Name,Address,TotalCar,TotalMotor,TotalBike FROM Parking_information" +
                    " WHERE (Name LIKE '%"+search.getText().toString()+"%\'"
                    +"OR Address LIKE '%"+search.getText().toString()+"%\')";
            switch (mode){
                case 0:
                    search_text += " AND TotalCar > 0";
                    break;
                case 1:
                    search_text += " AND TotalMotor > 0";
                    break;
                case 2:
                    search_text += " AND TotalBike > 0";
                    break;
            }
        }

        Cursor mcursor = draw.rawQuery(search_text, null);
        mcursor.moveToFirst();

        for(int i=0;i<mcursor.getCount();i++){
            parkingList.add(mcursor.getString(0)+","+mcursor.getString(1)+"," +
                    mcursor.getString(2)+","+mcursor.getString(3)+","+mcursor.getString(4));
            mcursor.moveToNext();
        }

        mcursor.close();

        parkingListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        draw.close();
    }
}
