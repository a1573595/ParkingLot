package com.example.puffer.parkingdemo;

import android.app.Activity;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;

import java.util.ArrayList;

public class ParkingListActivity extends AppCompatActivity {

    private Activity mActivity;

    private SQLiteDatabase draw;

    private ListView list;

    private ArrayList<String> parkingList;
    private ParkingListAdapter parkingListAdapter;

    private int mode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_list);
        mActivity = this;

        mode = getIntent().getIntExtra("mode",0);
        initDataBase();

        findView();
    }

    private void initDataBase(){
        MyDB mydb=new MyDB(this);
        draw = mydb.getWritableDatabase();
    }

    private void findView(){
        list = (ListView) findViewById(R.id.list);
    }

    private void initList(){
        parkingList = new ArrayList<>();

        String search_text;
        if(mode==1)
            search_text = "SELECT Name,Address,TotalCar,TotalMotor,TotalBike,ViewTime FROM Parking_history INNER JOIN Parking_information ON Parking_history.Info_id = Parking_information.Info_id ORDER BY Parking_history.ViewTime DESC";
        else
            search_text = "SELECT Name,Address,TotalCar,TotalMotor,TotalBike FROM Parking_love INNER JOIN Parking_information ON Parking_love.Info_id = Parking_information.Info_id";
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
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] test = parkingList.get(position).split(",");
                Intent intent = new Intent(mActivity,ParkingInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name",test[0]);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        initList();
        setListen();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
            System.gc();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        draw.close();
    }
}
