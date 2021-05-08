package com.a1573595.parkingdemo.main;

import com.a1573595.parkingdemo.BasePresenter;
import com.a1573595.parkingdemo.model.DataManager;
import com.a1573595.parkingdemo.model.ApiService;
import com.a1573595.parkingdemo.model.LatLngCoding;
import com.a1573595.parkingdemo.model.data.Parking;
import com.a1573595.parkingdemo.model.repository.ParkingDao;
import com.a1573595.parkingdemo.model.data.TCMSV_ALLDESC;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class MainPresenter extends BasePresenter<MainView> {
    public void readDataSet() {
        long updateTime = DataManager.getInstance().sp.readUpdateTime();
        // No dataSet in database
        if (updateTime < 1) {
            downloadDataSet();
        } else {
            addDisposable(DataManager.getInstance().getParkDao().getAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(view.showDataSetInfo()));
        }
    }

    public void downloadDataSet() {
        HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
        logger.level(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(logger)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                //.addConverterFactory(GsonConverterFactory.create()) // 使用 Gson 解析
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://tcgbusfs.blob.core.windows.net/blobtcmsv/")
                .client(client)
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        addDisposable(apiService.downloadFileWithDynamicUrlSync("TCMSV_alldesc.gz")
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<ResponseBody>() {
                    @Override
                    public void onSuccess(@NotNull ResponseBody responseBody) {
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

                            List<Parking> parkingList = new ArrayList<>();
                            String[] latlng;
                            for (TCMSV_ALLDESC.Data.Result park : parks) {
                                latlng = LatLngCoding.Cal_TWD97_To_lonlat(park.tw97x, park.tw97y).split(",");
                                parkingList.add(new Parking(park.id, park.area, park.name, park.summary,
                                        park.address, park.tel, park.payex, park.totalcar,
                                        park.totalmotor, park.totalbike, park.totalbus,
                                        Double.parseDouble(latlng[0]), Double.parseDouble(latlng[1])));
                            }

                            deleteDataSet(parkingList);
                        } catch (Exception ignored) {
                            view.showDataConversionFailed();
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        view.showDownloadFailed();
                    }
                }));
    }

    private void deleteDataSet(List<Parking> parkingList) {
        ParkingDao dao = DataManager.getInstance().getParkDao();

        addDisposable(dao.deleteAll()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        writeDataSet(parkingList);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                    }
                }));
    }

    private void writeDataSet(List<Parking> parkingList) {
        ParkingDao dao = DataManager.getInstance().getParkDao();

        addDisposable(dao.insertAll(parkingList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Long[]>() {
                    @Override
                    public void onSuccess(@NotNull Long[] longs) {
                        DataManager.getInstance().sp.setUpdateTime(System.currentTimeMillis());
                        readDataSet();
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                    }
                }));
    }
}