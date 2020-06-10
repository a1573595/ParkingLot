package com.a1573595.parkingdemo.update;

import com.a1573595.parkingdemo.BasePresenter;
import com.a1573595.parkingdemo.model.ApiService;
import com.a1573595.parkingdemo.model.DataManager;
import com.a1573595.parkingdemo.model.LatLngCoding;
import com.a1573595.parkingdemo.model.data.Parking;
import com.a1573595.parkingdemo.model.repository.ParkDao;
import com.a1573595.parkingdemo.model.data.TCMSV_ALLDESC;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class UpdatePresenter extends BasePresenter implements UpdateContract.Presenter {
    private UpdateContract.View view;

    void setView(UpdateContract.View view) {
        this.view = view;
    }

    @Override
    public void downloadDataSet() {
        Retrofit retrofit = new Retrofit.Builder()
                //.addConverterFactory(GsonConverterFactory.create()) // 使用 Gson 解析
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://tcgbusfs.blob.core.windows.net/blobtcmsv/")
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        addDisposable(apiService.downloadFileWithDynamicUrlSync("TCMSV_alldesc.gz")
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<ResponseBody>() {
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
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.updateFailed(e.getMessage());
                    }
                }));
    }

    private void deleteDataSet(List<Parking> parkingList) {
        ParkDao dao = DataManager.getInstance().getParkDao();

        addDisposable(dao.deleteAll()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        writeDataSet(parkingList);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                }));
    }

    private void writeDataSet(List<Parking> parkingList) {
        ParkDao dao = DataManager.getInstance().getParkDao();
        addDisposable(dao.insertAll(parkingList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Long[]>() {
                    @Override
                    public void onSuccess(Long[] longs) {
                        DataManager.getInstance().sp.setUpdateTime(System.currentTimeMillis());

                        view.updateFinished();
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.updateFailed(e.getMessage());
                    }
                }));
    }
}
