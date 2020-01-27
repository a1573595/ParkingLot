package com.example.puffer.parkingdemo.main;

import com.example.puffer.parkingdemo.BasePresenter;
import com.example.puffer.parkingdemo.model.DataManager;
import com.example.puffer.parkingdemo.model.ApiService;
import com.example.puffer.parkingdemo.model.LatLngCoding;
import com.example.puffer.parkingdemo.model.Park;
import com.example.puffer.parkingdemo.model.ParkDao;
import com.example.puffer.parkingdemo.model.TCMSV_ALLDESC;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

class MainPresenter extends BasePresenter implements MainContract.Presenter {
    private MainContract.View view;

    MainPresenter(MainContract.View view) {
        this.view = view;
    }

    @Override
    public void readDataSet() {
        long updateTime = DataManager.getInstance().sp.readUpdateTime();
        // No dataSet in database
        if(updateTime < 1) {
            downloadDataSet();
        } else {
            addDisposable(DataManager.getInstance().getParkDao().getAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(view.showDataSetInfo()));
        }
    }

    @Override
    public void downloadDataSet() {
        Retrofit retrofit = new Retrofit.Builder()
                //.addConverterFactory(GsonConverterFactory.create()) // 使用 Gson 解析
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://tcgbusfs.blob.core.windows.net/blobtcmsv/")
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.downloadFileWithDynamicUrlSync("TCMSV_alldesc.gz")
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new SingleObserver<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onSuccess(ResponseBody responseBody) {
                        try {
                            InputStream inputStream = responseBody.byteStream();
                            GZIPInputStream unGzip = new GZIPInputStream(inputStream);
                            ByteArrayOutputStream out = new ByteArrayOutputStream();

                            byte[] buffer = new byte[256];
                            int n;
                            while ((n = unGzip.read(buffer)) >= 0) {
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

                            writeDataSet(parkList);
                        } catch (Exception ignored) { }
                    }

                    @Override
                    public void onError(Throwable e) { }
                });
    }

    private void writeDataSet(List<Park> parkList) {
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
                    public void onError(Throwable e) { }
                });
    }
}
