package com.example.puffer.parkingdemo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.puffer.parkingdemo.DataClass.Parking;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
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

public class ParkingMapActivity extends AppCompatActivity implements
        ClusterManager.OnClusterClickListener<Parking>,
        ClusterManager.OnClusterItemClickListener<Parking>{

    private Activity mActivity;

    private MapFragment map;
    private GoogleMap mMap;

    private SQLiteDatabase draw;

    private ClusterManager<Parking> mClusterManager;
    private NonHierarchicalDistanceBasedAlgorithm clusterManagerAlgorithm;
    private LocationManager locationMgr;
    private LatLng mLatLng;

    private Geocoder geocoder;
    private List<Address> geecodeAddresse;

    private TextView address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_parking_map);

        findView();
        initLocationManager();

        map = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                if (ActivityCompat.checkSelfPermission(ParkingMapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
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

                initDataBase();
                mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        setParkingMark();
                    }
                });
            }
        });
    }

    private void findView(){
        address = (TextView) findViewById(R.id.address);
    }

    private void initLocationManager() {
        locationMgr = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        if (!locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || !locationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(mActivity, "請開啟定位服務", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            finish();
        }

        geocoder = new Geocoder(mActivity, Locale.getDefault());

        locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed())
                            locationMgr.removeUpdates(this);
                        else {
                            mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        }

                        try{
                            geecodeAddresse = geocoder.getFromLocation(mLatLng.latitude, mLatLng.longitude, 1);
                            address.setText(geecodeAddresse.get(0).getAddressLine(0));
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
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                //closeWindows();
            }
        });

        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);

        clusterManagerAlgorithm = new NonHierarchicalDistanceBasedAlgorithm();
        mClusterManager.setAlgorithm(clusterManagerAlgorithm);
    }

    private void initDataBase(){
        MyDB mydb=new MyDB(this);
        draw = mydb.getWritableDatabase();
    }

    private void setParkingMark(){
        String search_text = "SELECT Name,Area,Latitude,Longitude,TotalCar,TotalMotor,TotalBike FROM Parking_point INNER JOIN Parking_information ON Parking_point.Point_id = Parking_information.Info_id";
        Cursor mcursor = draw.rawQuery(search_text, null);
        mcursor.moveToFirst();

        for(int i=0;i<mcursor.getCount();i++){
            mClusterManager.addItem(new Parking(new LatLng(mcursor.getDouble(2),mcursor.getDouble(3)),
                    mcursor.getString(0), mcursor.getString(1),
                    mcursor.getInt(4), mcursor.getInt(5), mcursor.getInt(6)));
            mcursor.moveToNext();
        }

        mcursor.close();
        mClusterManager.cluster();
        mMap.getUiSettings().setAllGesturesEnabled(true);
    }

    private class ParkingRender extends DefaultClusterRenderer<Parking> {
        public ParkingRender() {
            super(getApplicationContext(), mMap, mClusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(Parking parking, MarkerOptions markerOptions) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.parking_icon2));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            return cluster.getSize() > 1;
        }
    }

    @Override
    public boolean onClusterClick(Cluster<Parking> cluster) {
        if(mMap.getCameraPosition().zoom==mMap.getMaxZoomLevel()){
            Log.e("test","有兩個點");
            showChoiceDialog(cluster);
        }

        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (Parking item : cluster.getItems()) {
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

    private void showChoiceDialog(Cluster<Parking> cluster){
        final AlertDialog choiceDialog= new AlertDialog.Builder(this).create();
        choiceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        choiceDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        choiceDialog.show();

        choiceDialog.setContentView(R.layout.choice_dialog_list);
        TextView titletext = (TextView) choiceDialog.findViewById(R.id.titletext);
        titletext.setText(String.format("包含%d個停車場",cluster.getSize()));
        ListView listView = (ListView) choiceDialog.findViewById(R.id.listView);
        final ArrayList<String> list = new ArrayList<>();
        final ArrayList<Parking> station = new ArrayList<>();
        for (Parking item : cluster.getItems()) {
            list.add(item.name);
            station.add(item);
        }
        choiceDialogAdapter adapter = new choiceDialogAdapter(this,list);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                showDialog(station.get(position));
                choiceDialog.cancel();
            }
        });
    }

    @Override
    public boolean onClusterItemClick(Parking item) {
        showDialog(item);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(item.getPosition()));
        return true;
    }

    private void showDialog(final Parking parking){
        final AlertDialog dialog= new AlertDialog.Builder(this).create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.getWindow().setDimAmount(0f);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rectangle_gray);
        dialog.show();

        dialog.setContentView(R.layout.parking_info_windows_layout);
        LinearLayout info_layout = (LinearLayout) dialog.findViewById(R.id.info_layout);
        TextView name = (TextView) dialog.findViewById(R.id.name);
        TextView address = (TextView) dialog.findViewById(R.id.address);
        TextView total= (TextView) dialog.findViewById(R.id.total);

        name.setText(parking.name);
        address.setText(parking.area);
        total.setText(String.format("轎車:%d / 機車:%d / 自行車:%d",
                parking.totalCar,parking.totalMotor,parking.totalBike));

        info_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity,ParkingInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name",parking.name);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
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
