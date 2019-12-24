package com.example.puffer.parkingdemo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.puffer.parkingdemo.DataClass.LatLngCoding;
import com.example.puffer.parkingdemo.DataClass.Taipei_Parking_Info;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 2;
    private static final String REGEX_INPUT_BOUNDARY_BEGINNING = "\\A";

    private SQLiteDatabase draw;

    private TextView tv_dataset, tv_map, tv_list, tv_love, tv_history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_dataset = findViewById(R.id.tv_dataset);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            return;
        }

        try{
            read();
            findView();
            setListen();
        }catch (Exception e){
            Log.e("test_error",e.toString());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case REQUEST_LOCATION:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //取得權限
                    try{
                        read();
                        findView();
                        setListen();
                    }catch (Exception e){
                        Log.e("test_error",e.toString());
                    }
                } else {
                    //使用者拒絕權限
                    finish();
                }
        }
    }

    public void read() throws Exception{
        Calendar c = Calendar.getInstance();
        //Log.e("test","Current time =&gt; "+c.getTime());


        MyDB mydb=new MyDB(this);
        draw = mydb.getWritableDatabase();

        String count = "SELECT COUNT(*) FROM Parking_point";
        Cursor mcursor = draw.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        Log.e("test",String.valueOf(icount));
        mcursor.close();

        Cursor mcursor2 = draw.rawQuery("SELECT CreateTime FROM Parking_data WHERE TableName LIKE 'Parking_point'", null);

        mcursor2.moveToFirst();
        if(mcursor2.getCount()>0){
            long time = mcursor2.getLong(0);
            c.setTimeInMillis(time);
            Log.e("test","Current time =&gt; "+c.getTime());
        }

        mcursor2.close();

        if(icount>0) {
            tv_dataset.setText(String.format("總收錄%d筆資料\n建立於%d/%02d/%02d  %02d:%02d:%02d",icount,
                    c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH),
                    c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),c.get(Calendar.SECOND)));
            return;
        }

        InputStream inputStream = getResources().openRawResource(R.raw.taipei_parking_info);
        String stream = new Scanner(inputStream).useDelimiter(REGEX_INPUT_BOUNDARY_BEGINNING).next();
        Log.e("test_String",stream);

        JSONObject object = new JSONObject(stream);
        Log.e("test_jsonObject",object.toString());

        Taipei_Parking_Info taipei_parking_info = new Gson().fromJson(object.toString(),Taipei_Parking_Info.class);
        tv_dataset.setText(String.format("共有%d筆資料",taipei_parking_info.parkings.length));

        String sql = "INSERT OR REPLACE INTO Parking_point(Latitude, Longitude) VALUES (?, ?)";
        String sql2 = "INSERT OR REPLACE INTO Parking_information(Name, Area, Address, Summary, " +
                "Telephone, PayInfo, TotalCar, TotalMotor, TotalBike) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        draw.beginTransactionNonExclusive();

        SQLiteStatement stmt = draw.compileStatement(sql);
        SQLiteStatement stmt2 = draw.compileStatement(sql2);
        String[] latlng;

        for(Taipei_Parking_Info.Result result:taipei_parking_info.parkings){
            if(result.name==null || result.name.equals("") ||
                    result.address==null || result.address.equals("") ||
                    result.tw97x== 0.0 || result.tw97y== 0.0 ||
                    result.tel==null || result.tel.equals("") ||
                    (result.totalcar==0 && result.totalmotor==0 && result.totalbike==0)) continue;

            latlng = LatLngCoding.Cal_TWD97_To_lonlat(result.tw97x,result.tw97y).split(",");

            stmt.bindDouble(1,Double.valueOf(latlng[0]));
            stmt.bindDouble(2,Double.valueOf(latlng[1]));
            stmt.execute();
            stmt.clearBindings();

            stmt2.bindString(1,result.name);
            stmt2.bindString(2,result.area);
            stmt2.bindString(3,result.address);
            stmt2.bindString(4,result.summary);
            stmt2.bindString(5,result.tel);
            stmt2.bindString(6,result.payex);
            stmt2.bindLong(7,result.totalcar);
            stmt2.bindLong(8,result.totalmotor);
            stmt2.bindLong(9,result.totalbike);
            stmt2.execute();
            stmt2.clearBindings();
        }
        
        draw.setTransactionSuccessful();
        draw.endTransaction();

        draw.execSQL("INSERT OR REPLACE INTO Parking_data(TableName,CreateTime) VALUES(?, ?)",new Object[]{"Parking_point",System.currentTimeMillis()});
        draw.execSQL("INSERT OR REPLACE INTO Parking_data(TableName,CreateTime) VALUES(?, ?)",new Object[]{"Parking_information",System.currentTimeMillis()});
        draw.close();
    }

    private void findView(){
        tv_map = findViewById(R.id.tv_map);
        tv_list = findViewById(R.id.tv_list);
        tv_love = findViewById(R.id.tv_love);
        tv_history = findViewById(R.id.tv_history);
    }

    private void setListen(){
        tv_map.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this,ParkingMapActivity.class);
            startActivity(i);
        });

        tv_list.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this,ParkingFuzzySearchActivity.class);
            startActivity(i);
        });

        tv_love.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this,ParkingListActivity.class);
            i.putExtra("mode",0);
            startActivity(i);
        });

        tv_history.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this,ParkingListActivity.class);
            i.putExtra("mode",1);
            startActivity(i);
        });
    }
}
