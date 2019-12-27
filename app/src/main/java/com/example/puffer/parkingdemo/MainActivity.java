package com.example.puffer.parkingdemo;

import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.puffer.parkingdemo.model.LatLngCoding;
import com.example.puffer.parkingdemo.model.TCMSV_ALLDESC;
import com.example.puffer.parkingdemo.model.ApiService;
import com.example.puffer.parkingdemo.model.DataManager;
import com.example.puffer.parkingdemo.model.Park;
import com.example.puffer.parkingdemo.model.ParkDao;
import com.example.puffer.parkingdemo.ParkFuzzySearch.ParkFuzzySearchActivity;
import com.example.puffer.parkingdemo.parkList.ParkListActivity;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.zip.GZIPInputStream;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION = 2;

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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case REQUEST_LOCATION:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    findView();
                    readDataSet();
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
            downloadDataSet();
        } else {
            DataManager.getInstance().getParkDao().getAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Park[]>() {
                        @Override
                        public void onSubscribe(Disposable d) { }

                        @Override
                        public void onSuccess(Park[] parks) {
                            Calendar c = Calendar.getInstance();
                            c.setTimeInMillis(updateTime);

                            tv_dataset.setText(String.format("總收錄%d筆資料\n建立於%d/%02d/%02d  %02d:%02d:%02d", parks.length,
                                    c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH),
                                    c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),c.get(Calendar.SECOND)));

                            setListen();
                        }

                        @Override
                        public void onError(Throwable e) {
                            tv_dataset.setText(e.toString());
                        }
                    });
        }
    }

    private void downloadDataSet() {
        Retrofit retrofit = new Retrofit.Builder()
                //.addConverterFactory(GsonConverterFactory.create()) // 使用 Gson 解析
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://tcgbusfs.blob.core.windows.net/blobtcmsv/")
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.downloadFileWithDynamicUrlSync("TCMSV_alldesc.gz")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onSuccess(ResponseBody responseBody) {
                        try {
                            InputStream inputStream = responseBody.byteStream();
                            GZIPInputStream ungzip = new GZIPInputStream(inputStream);
                            ByteArrayOutputStream out = new ByteArrayOutputStream();

                            byte[] buffer = new byte[256];
                            int n;
                            while ((n = ungzip.read(buffer)) >= 0) {
                                out.write(buffer, 0, n);
                            }

                            TCMSV_ALLDESC parking_info = new Gson().fromJson(out.toString("UTF-8"), TCMSV_ALLDESC.class);
                            TCMSV_ALLDESC.Data.Result[] parks = parking_info.data.park;

                            List<Park> parkList = new ArrayList<>();
                            String[] latlng;
                            for (TCMSV_ALLDESC.Data.Result park : parks) {
                                latlng = LatLngCoding.Cal_TWD97_To_lonlat(park.tw97x, park.tw97y).split(",");
                                parkList.add(new Park(park.id, park.area, park.name, park.summary,
                                        park.address, park.tel, park.payex, park.totalcar,
                                        park.totalmotor, park.totalbike, park.totalbus,
                                        Double.valueOf(latlng[0]), Double.valueOf(latlng[1])));
                            }

                            ParkDao dao = DataManager.getInstance().getParkDao();
                            dao.insertAll(parkList)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<Long[]>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {
                                            dao.deleteAll();
                                        }

                                        @Override
                                        public void onSuccess(Long[] longs) {
                                            DataManager.getInstance().sp.setUpdateTime(System.currentTimeMillis());
                                            readDataSet();
                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }
                                    });
                        }catch (Exception e) {
                            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
            i.putExtra("isLove",true);
            startActivity(i);
        });

        tv_history.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ParkListActivity.class);
            i.putExtra("isLove",false);
            startActivity(i);
        });
    }
}
