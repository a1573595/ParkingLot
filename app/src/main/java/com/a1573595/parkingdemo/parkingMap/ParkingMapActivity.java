package com.a1573595.parkingdemo.parkingMap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.a1573595.parkingdemo.BaseActivity;
import com.a1573595.parkingdemo.R;
import com.a1573595.parkingdemo.databinding.ActivityParkingMapBinding;
import com.a1573595.parkingdemo.model.data.Parking;
import com.a1573595.parkingdemo.model.data.ParkingCluster;
import com.a1573595.parkingdemo.parkingInfo.ParkingInfoActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.collections.MarkerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.observers.DisposableSingleObserver;

public class ParkingMapActivity extends BaseActivity implements ParkingMapContract.View,
        GoogleMap.OnCameraIdleListener, GoogleMap.OnMarkerClickListener,
        ClusterManager.OnClusterClickListener<ParkingCluster>,
        ClusterManager.OnClusterItemClickListener<ParkingCluster> {
    private ParkingMapPresenter presenter;

    private GoogleMap mMap;
    private ClusterManager<ParkingCluster> mClusterManager;
    private ParkingRender renderer;
    private LocationManager locationMgr;
    private LatLng mLatLng;

    private Geocoder geocoder;
    private List<Address> geocodeAddress;

    private ActivityParkingMapBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = ActivityParkingMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initLocationManager();

        MapFragment map = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(googleMap -> {
            mMap = googleMap;

            if (ActivityCompat.checkSelfPermission(ParkingMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(ParkingMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                finish();
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(false);
            initClusterManager();

//            if (mLatLng != null)
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 15));
//            else
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(25.0329694, 121.56541770000001), 15));

            mMap.setOnMapLoadedCallback(() -> presenter.readDataSet());
        });
    }

    @Override
    protected void createPresenter() {
        presenter = ViewModelProviders.of(this).get(ParkingMapPresenter.class);
        presenter.setView(this);
    }

    @Override
    public DisposableSingleObserver<Parking[]> showParkMark() {
        return new DisposableSingleObserver<Parking[]>() {
            @Override
            public void onSuccess(Parking[] parkings) {
                for (Parking parking : parkings) {
                    mClusterManager.addItem(new ParkingCluster(new LatLng(parking.lat, parking.lng), parking.id,
                            parking.name, parking.area, parking.totalcar, parking.totalmotor, parking.totalbike,
                            parking.totalbus));
                }

                mClusterManager.cluster();
                mMap.getUiSettings().setAllGesturesEnabled(true);
            }

            @Override
            public void onError(Throwable e) {
            }
        };
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }

    @Override
    public boolean onClusterClick(Cluster<ParkingCluster> cluster) {
        if (mMap.getCameraPosition().zoom == mMap.getMaxZoomLevel()) {
            showChoiceDialog(cluster);
        }

        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ParkingCluster item : cluster.getItems()) {
            builder.include(item.getPosition());
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

    @Override
    public boolean onClusterItemClick(ParkingCluster item) {
        showDialog(item);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(item.getPosition()));
        return true;
    }

    private void initLocationManager() {
        locationMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
//        if (!locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)
//                || !locationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//
//            new MaterialAlertDialogBuilder(this, R.style.DialogTheme)
//                    .setTitle(R.string.note)
//                    .setMessage(R.string.please_open_gps)
//                    .setCancelable(false)
//                    .setPositiveButton(R.string.agree, (dialogInterface, i) ->
//                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
//                    .setNegativeButton(R.string.cancel, (dialogInterface, i) ->
//                            dialogInterface.dismiss())
//                    .show();
//        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            finish();
        }

        geocoder = new Geocoder(this, Locale.getDefault());

        locationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (isFinishing() || isDestroyed()) {
                            locationMgr.removeUpdates(this);
                        } else {
                            mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        }

                        try {
                            geocodeAddress = geocoder.getFromLocation(mLatLng.latitude, mLatLng.longitude, 1);
                            binding.tvAddress.setText(getString(R.string.Current_location, geocodeAddress.get(0).getAddressLine(0)));
                        } catch (Exception e) {
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
        if (location != null)
            mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    private void initClusterManager() {
        mClusterManager = new ClusterManager<>(this, mMap);
        mClusterManager.setRenderer(renderer = new ParkingRender());
        mMap.setOnCameraIdleListener(this);
//        mMap.setOnCameraIdleListener(mClusterManager);
//        mMap.setOnMarkerClickListener(mClusterManager);
//        mMap.setOnInfoWindowClickListener(mClusterManager);

        MarkerManager.Collection markerCollection = mClusterManager.getMarkerManager().newCollection();
        markerCollection.addMarker(new MarkerOptions().position(new LatLng(25.0329694, 121.56541770000001)));
        markerCollection.setOnMarkerClickListener(this);

        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        mClusterManager.setAlgorithm(new NonHierarchicalViewBasedAlgorithm<>(dm.widthPixels, dm.heightPixels));
    }

    @Override
    public void onCameraIdle() {
        mClusterManager.onCameraIdle();
    }

    private class ParkingRender extends DefaultClusterRenderer<ParkingCluster> {
        ParkingRender() {
            super(getApplicationContext(), mMap, mClusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(ParkingCluster parkingCluster, MarkerOptions markerOptions) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
        }

//        @Override
//        protected void onBeforeClusterRendered(@NonNull Cluster<ParkingCluster> cluster, @NonNull MarkerOptions markerOptions) {
//            super.onBeforeClusterRendered(cluster, markerOptions);
//        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            return cluster.getSize() > 2;
        }
    }

    private void showChoiceDialog(Cluster<ParkingCluster> cluster) {
        final AlertDialog choiceDialog = new AlertDialog.Builder(this).create();
        choiceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        choiceDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        choiceDialog.show();

        choiceDialog.setContentView(R.layout.choice_dialog_list);
        TextView tv_title = choiceDialog.findViewById(R.id.tv_title);
        tv_title.setText(String.format(getString(R.string.include_parking), cluster.getSize()));
        ListView listView = choiceDialog.findViewById(R.id.listView);
        final ArrayList<String> list = new ArrayList<>();
        final ArrayList<ParkingCluster> station = new ArrayList<>();
        for (ParkingCluster item : cluster.getItems()) {
            list.add(item.name);
            station.add(item);
        }
        presenter.putItems(list);
        choiceDialogAdapter adapter = new choiceDialogAdapter(presenter);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view, position, l) -> {
            choiceDialog.dismiss();

            showDialog(station.get(position));
        });
    }

    private void showDialog(final ParkingCluster parkingCluster) {
        final AlertDialog dialog = new MaterialAlertDialogBuilder(this, R.style.DialogTheme).create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        dialog.setContentView(R.layout.parking_info_windows_layout);
        LinearLayout info_layout = dialog.findViewById(R.id.ll_info);
        TextView name = dialog.findViewById(R.id.tv_name);
        TextView address = dialog.findViewById(R.id.tv_address);
        TextView total = dialog.findViewById(R.id.tv_total);

        name.setText(parkingCluster.name);
        address.setText(parkingCluster.area);
        total.setText(getString(R.string.transportation, parkingCluster.totalBus, parkingCluster.totalCar,
                parkingCluster.totalMotor, parkingCluster.totalBike));

        info_layout.setOnClickListener(v -> {
            Intent intent = new Intent(this, ParkingInfoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", parkingCluster.id);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }
}
