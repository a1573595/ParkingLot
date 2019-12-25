package com.example.puffer.parkingdemo;

import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.puffer.parkingdemo.DataClass.LatLngCoding;
import com.example.puffer.parkingdemo.DataClass.Taipei_Parking_Info;
import com.example.puffer.parkingdemo.model.DataManager;
import com.example.puffer.parkingdemo.model.Park;
import com.example.puffer.parkingdemo.model.ParkDao;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION = 2;
    private static final String REGEX_INPUT_BOUNDARY_BEGINNING = "\\A";

    private TextView tv_dataset, tv_map, tv_list, tv_love, tv_history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataManager.getInstance().initDatabase(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            return;
        }

        findView();
        readDataSet();
        setListen();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case REQUEST_LOCATION:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    findView();
                    readDataSet();
                    setListen();
                } else {    //使用者拒絕權限
                    finish();
                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        DataManager.getInstance().closeDatabase();
    }

    private void findView(){
        tv_dataset = findViewById(R.id.tv_dataset);
        tv_map = findViewById(R.id.tv_map);
        tv_list = findViewById(R.id.tv_list);
        tv_love = findViewById(R.id.tv_love);
        tv_history = findViewById(R.id.tv_history);
    }

    private void readDataSet() {
        long updateTime = DataManager.getInstance().sp.readUpdateTime();
        // No dataSet in database
        if(updateTime < 1) {
            try {
                InputStream stream_parking = getResources().openRawResource(R.raw.taipei_park_info);
                String json = new Scanner(stream_parking).useDelimiter(REGEX_INPUT_BOUNDARY_BEGINNING).next();

                JSONObject jsonObject = new JSONObject(json);
                Taipei_Parking_Info parking_info = new Gson().fromJson(jsonObject.toString(), Taipei_Parking_Info.class);
                Taipei_Parking_Info.Data.Result[] parks = parking_info.data.park;

//                JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("park");
//                Type listType = new TypeToken<List<Park>>(){}.getType();
//                List<Park> parkList = new Gson().fromJson(jsonArray.toString(), listType);

                List<Park> parkList = new ArrayList<>();
                String[] latlng;
                for (Taipei_Parking_Info.Data.Result park : parks) {
                    latlng = LatLngCoding.Cal_TWD97_To_lonlat(park.tw97x, park.tw97y).split(",");
                    parkList.add(new Park(park.id, park.area, park.name, park.summary,
                            park.address, park.tel, park.payex, park.totalcar,
                            park.totalmotor, park.totalbike, park.totalbus,
                            Double.valueOf(latlng[0]), Double.valueOf(latlng[1])));
                }

                ParkDao dao = DataManager.getInstance().getParkDao();
                dao.deleteAll();
                dao.insertAll(parkList);
                DataManager.getInstance().sp.setUpdateTime(System.currentTimeMillis());
            }catch (RuntimeException e){
                Log.e("test-run", e.toString());
            }catch (JSONException e){
                Log.e("test-JSON", e.toString());
            }
        }

        Park[] parks = DataManager.getInstance().getParkDao().getAll();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(updateTime > 0 ? updateTime : System.currentTimeMillis());

        tv_dataset.setText(String.format("總收錄%d筆資料\n建立於%d/%02d/%02d  %02d:%02d:%02d", parks.length,
                c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH),
                c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),c.get(Calendar.SECOND)));
    }

    private void setListen(){
        tv_map.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ParkingMapActivity.class);
            startActivity(i);
        });

        tv_list.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ParkFuzzySearchActivity.class);
            startActivity(i);
        });

        tv_love.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ParkListActivity.class);
            i.putExtra("mode",0);
            startActivity(i);
        });

        tv_history.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ParkListActivity.class);
            i.putExtra("mode",1);
            startActivity(i);
        });
    }
}
