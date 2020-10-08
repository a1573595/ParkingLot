package com.a1573595.parkingdemo.parkingMap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.BaseColumns;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import com.a1573595.parkingdemo.BaseActivity;
import com.a1573595.parkingdemo.R;
import com.a1573595.parkingdemo.databinding.ActivityParkingMapBinding;
import com.a1573595.parkingdemo.databinding.ParkingInfoWindowsLayoutBinding;
import com.a1573595.parkingdemo.model.data.Parking;
import com.a1573595.parkingdemo.model.data.ParkingCluster;
import com.a1573595.parkingdemo.parkingInfo.ParkingInfoActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
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

import java.util.List;
import java.util.Locale;

import io.reactivex.observers.DisposableSingleObserver;

public class ParkingMapActivity extends BaseActivity<ParkingMapPresenter> implements ParkingMapView,
        GoogleMap.OnCameraIdleListener, GoogleMap.OnMarkerClickListener,
        ClusterManager.OnClusterClickListener<ParkingCluster>,
        ClusterManager.OnClusterItemClickListener<ParkingCluster> {
    private ActivityParkingMapBinding binding;
    private SupportMapFragment mapFragment;

    private GoogleMap mMap;
    private ClusterManager<ParkingCluster> mClusterManager;
    private MarkerManager.Collection normalMarkerManager;

    private static final float Max_Clustering_Room_Level = 17f;
    private float zoomLevel = 0;

    private Geocoder geocoder;
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private List<Address> geocodeAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = ActivityParkingMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;

            mMap.getUiSettings().setAllGesturesEnabled(false);
            mMap.setOnCameraIdleListener(this);

            initLocationBtn();
            initClusterManager();
            initSearchView();

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(25.0329694, 121.56541770000001), 15));
            mMap.setOnMapLoadedCallback(() -> presenter.readDataSet());
        });
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
        Log.d(getClass().getSimpleName(), "onMarkerClick");

        marker.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

        return true;
    }

    @Override
    public boolean onClusterClick(Cluster<ParkingCluster> cluster) {
        Log.d(getClass().getSimpleName(), "onClusterClick");

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
        Log.d(getClass().getSimpleName(), "onClusterItemClick");

        // If you want, you can get cluster marker from render and call onMarkerClick
//        Marker clickedMarker = renderer.getMarker(item);
//        onMarkerClick(clickedMarker);

        showDialog(item);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(item.getPosition()));
        return true;
    }

    @Override
    public void onCameraIdle() {
        zoomLevel = mMap.getCameraPosition().zoom;
        mClusterManager.onCameraIdle();
    }

    private void initLocationBtn() {
        if (ActivityCompat.checkSelfPermission(ParkingMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(ParkingMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            finish();
        }

        mMap.setMyLocationEnabled(true);

        View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        int pixel = (int) getResources().getDisplayMetrics().density;

        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(0, pixel * 80, pixel * 48, 0);
    }

    private void initClusterManager() {
        mClusterManager = new ClusterManager<>(this, mMap);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mClusterManager.setAlgorithm(new NonHierarchicalViewBasedAlgorithm<>(dm.widthPixels, dm.heightPixels));
        mClusterManager.setRenderer(new ParkingRender());
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);

        // Use new collection to add single marker without clustering
        normalMarkerManager = mClusterManager.getMarkerManager().newCollection();
        normalMarkerManager.setOnMarkerClickListener(this);
    }

    private void initSearchView() {
        geocoder = new Geocoder(this, Locale.getDefault());

        final String[] from = new String[]{"address", "lat", "lng"};
        final int[] to = new int[]{android.R.id.text1};
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,
                null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        binding.searchView.setSuggestionsAdapter(simpleCursorAdapter);
        // SearchView whole clickable
        binding.searchView.setOnClickListener(v -> binding.searchView.setIconified(false));

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // delay when user typing
                searchHandler.removeCallbacksAndMessages(null);
                searchHandler.postDelayed(() -> {
                    try {
                        geocodeAddress = geocoder.getFromLocationName(binding.searchView.getQuery().toString(), 5);

                        final MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, "address", "lat", "lng"});
                        for (int i = 0; i < geocodeAddress.size(); i++) {
                            c.addRow(new Object[]{i, geocodeAddress.get(i).getAddressLine(0),
                                    geocodeAddress.get(i).getLatitude(), geocodeAddress.get(i).getLongitude()});
                        }
                        simpleCursorAdapter.changeCursor(c);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, 750);

                return false;
            }
        });

        binding.searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) simpleCursorAdapter.getItem(position);
                String address = cursor.getString(cursor.getColumnIndex("address"));
                double lat = cursor.getDouble(cursor.getColumnIndex("lat"));
                double lng = cursor.getDouble(cursor.getColumnIndex("lng"));

                normalMarkerManager.clear();
                Marker marker = normalMarkerManager.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(address));
                marker.showInfoWindow();

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15));

                return false;
            }
        });

        binding.searchView.setQueryHint(getString(R.string.query_hint));
        binding.searchView.setVisibility(View.VISIBLE);
    }

    private void showDialog(final ParkingCluster parkingCluster) {
        final AlertDialog dialog = new MaterialAlertDialogBuilder(this, R.style.DialogTheme).create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        ParkingInfoWindowsLayoutBinding binding = ParkingInfoWindowsLayoutBinding.inflate(LayoutInflater.from(this));
        dialog.setContentView(binding.getRoot());

        binding.tvName.setText(parkingCluster.name);
        binding.tvAddress.setText(parkingCluster.area);
        binding.tvTotal.setText(getString(R.string.transportation, parkingCluster.totalBus, parkingCluster.totalCar,
                parkingCluster.totalMotor, parkingCluster.totalBike));

        binding.llInfo.setOnClickListener(v -> {
            Intent intent = new Intent(this, ParkingInfoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", parkingCluster.id);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }

    private class ParkingRender extends DefaultClusterRenderer<ParkingCluster> {
        ParkingRender() {
            super(getApplicationContext(), mMap, mClusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(ParkingCluster parkingCluster, MarkerOptions markerOptions) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            return zoomLevel < Max_Clustering_Room_Level && super.shouldRenderAsCluster(cluster);
        }
    }
}
