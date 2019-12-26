package com.example.puffer.parkingdemo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.puffer.parkingdemo.model.ParkCluster;
import com.example.puffer.parkingdemo.model.DataManager;
import com.example.puffer.parkingdemo.model.Park;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ParkingMapActivity extends AppCompatActivity implements
        ClusterManager.OnClusterClickListener<ParkCluster>,
        ClusterManager.OnClusterItemClickListener<ParkCluster>{
    private MapFragment map;
    private GoogleMap mMap;

    private ClusterManager<ParkCluster> mClusterManager;
    private NonHierarchicalDistanceBasedAlgorithm clusterManagerAlgorithm;
    private LocationManager locationMgr;
    private LatLng mLatLng;

    private Geocoder geocoder;
    private List<Address> geecodeAddresse;

    private TextView tv_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_parking_map);

        findView();
        initLocationManager();

        map = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(googleMap -> {
            mMap = googleMap;

            if (ActivityCompat.checkSelfPermission(ParkingMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(ParkingMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                finish();
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(false);
            initClusterManager();

            if(mLatLng!=null)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 15));
            else
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(25.0329694, 121.56541770000001), 15));

            mMap.setOnMapLoadedCallback(() -> setParkingMark());
        });
    }

    private void findView(){
        tv_address = findViewById(R.id.tv_address);
    }

    private void initLocationManager() {
        locationMgr = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        if (!locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || !locationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            finish();
        }

        geocoder = new Geocoder(this, Locale.getDefault());

        locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (isFinishing() || isDestroyed())
                            locationMgr.removeUpdates(this);
                        else {
                            mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        }

                        try{
                            geecodeAddresse = geocoder.getFromLocation(mLatLng.latitude, mLatLng.longitude, 1);
                            tv_address.setText("目前位置 :\n" + geecodeAddresse.get(0).getAddressLine(0));
                        }catch (Exception e){
                            e.toString();
                        }
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                    }
                });

        Location location = locationMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location != null)
        mLatLng = new LatLng(location.getLatitude(),location.getLongitude());
    }

    private void initClusterManager(){
        mClusterManager = new ClusterManager<>(this, mMap);
        mClusterManager.setRenderer(new ParkingRender());
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        mMap.setOnCameraMoveListener(() -> {
            //closeWindows();
        });

        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);

        clusterManagerAlgorithm = new NonHierarchicalDistanceBasedAlgorithm();
        mClusterManager.setAlgorithm(clusterManagerAlgorithm);
    }

    private void setParkingMark(){
        DataManager.getInstance().getParkDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Park[]>() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onSuccess(Park[] parks) {
                        for(Park park: parks) {
                            mClusterManager.addItem(new ParkCluster(new LatLng(park.lat, park.lng), park.id,
                                    park.name, park.area, park.totalcar, park.totalmotor, park.totalbike,
                                    park.totalbus));
                        }

                        mClusterManager.cluster();
                        mMap.getUiSettings().setAllGesturesEnabled(true);
                    }

                    @Override
                    public void onError(Throwable e) { }
                });
    }

    private class ParkingRender extends DefaultClusterRenderer<ParkCluster> {
        public ParkingRender() {
            super(getApplicationContext(), mMap, mClusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(ParkCluster parkCluster, MarkerOptions markerOptions) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.parking_icon2));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            return cluster.getSize() > 1;
        }
    }

    @Override
    public boolean onClusterClick(Cluster<ParkCluster> cluster) {
        if(mMap.getCameraPosition().zoom==mMap.getMaxZoomLevel()){
            Log.e("test","有兩個點");
            showChoiceDialog(cluster);
        }

        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ParkCluster item : cluster.getItems()) {
            builder.include(item.getPosition());
            Log.e("test",item.name);
        }
        // Get the LatLngBounds
        final LatLngBounds bounds = builder.build();

        // Animate camera to the bounds
        try {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void showChoiceDialog(Cluster<ParkCluster> cluster){
        final AlertDialog choiceDialog= new AlertDialog.Builder(this).create();
        choiceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        choiceDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        choiceDialog.show();

        choiceDialog.setContentView(R.layout.choice_dialog_list);
        TextView titletext = choiceDialog.findViewById(R.id.tv_title);
        titletext.setText(String.format("包含%d個停車場",cluster.getSize()));
        ListView listView = choiceDialog.findViewById(R.id.listView);
        final ArrayList<String> list = new ArrayList<>();
        final ArrayList<ParkCluster> station = new ArrayList<>();
        for (ParkCluster item : cluster.getItems()) {
            list.add(item.name);
            station.add(item);
        }
        choiceDialogAdapter adapter = new choiceDialogAdapter(this,list);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view, position, l) -> {
            showDialog(station.get(position));
            choiceDialog.cancel();
        });
    }

    @Override
    public boolean onClusterItemClick(ParkCluster item) {
        showDialog(item);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(item.getPosition()));
        return true;
    }

    private void showDialog(final ParkCluster parkCluster){
        final AlertDialog dialog= new AlertDialog.Builder(this).create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.getWindow().setDimAmount(0f);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rectangle_gray);
        dialog.show();

        dialog.setContentView(R.layout.parking_info_windows_layout);
        LinearLayout info_layout = dialog.findViewById(R.id.ll_info);
        TextView name = dialog.findViewById(R.id.tv_name);
        TextView address = dialog.findViewById(R.id.tv_address);
        TextView total= dialog.findViewById(R.id.tv_total);

        name.setText(parkCluster.name);
        address.setText(parkCluster.area);
        total.setText(String.format("轎車:%d / 機車:%d / 自行車:%d",
                parkCluster.totalCar, parkCluster.totalMotor, parkCluster.totalBike));

        info_layout.setOnClickListener(v -> {
            Intent intent = new Intent(this, ParkInfoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", parkCluster.name);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }
}
