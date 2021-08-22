package com.a1573595.parkingdemo.main;

import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.a1573595.parkingdemo.BaseActivity;
import com.a1573595.parkingdemo.databinding.ActivityMainBinding;
import com.a1573595.parkingdemo.model.DataManager;
import com.a1573595.parkingdemo.model.data.Parking;
import com.a1573595.parkingdemo.parkingMap.ParkingMapActivity;
import com.a1573595.parkingdemo.R;
import com.a1573595.parkingdemo.parkingFuzzySearch.ParkingFuzzySearchActivity;
import com.a1573595.parkingdemo.parkingList.ParkingListActivity;
import com.a1573595.parkingdemo.update.UpdateActivity;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.observers.DisposableSingleObserver;

public class MainActivity extends BaseActivity<MainPresenter> implements MainView {
    private static final int REQUEST_LOCATION = 2;

    private ActivityMainBinding binding;

    private final Handler backHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DataManager.getInstance().initDatabase(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            return;
        }

        presenter.readDataSet();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                presenter.readDataSet();
            } else {    //使用者拒絕權限
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.btn_update) {
            transitionToUpdate();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        presenter.readDataSet();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        DataManager.getInstance().closeDatabase();
    }

    @Override
    public void onBackPressed() {
        if (backHandler.hasMessages(0)) {
            finish();
        } else {
            Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show();
            backHandler.removeCallbacksAndMessages(null);
            backHandler.postDelayed(() -> {
            }, 2000);
        }
    }

    @Override
    public void transitionToUpdate() {
        Pair<View, String>[] pairs = new Pair[3];
        pairs[0] = Pair.create(binding.imageView, "imageView");
        pairs[1] = Pair.create(binding.textView, "textView");
        pairs[2] = Pair.create(binding.tvDataset, "tv_dataset");

        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(MainActivity.this, pairs);

        Intent intent = new Intent(this, UpdateActivity.class);
        startActivity(intent, options.toBundle());
    }

    @Override
    public DisposableSingleObserver<Parking[]> showDataSetInfo() {
        return new DisposableSingleObserver<Parking[]>() {
            @Override
            public void onSuccess(@NotNull Parking[] parkings) {
                String date = convertLongToTime(DataManager.getInstance().sp.readUpdateTime());
                binding.tvDataset.setText(String.format(getString(R.string.total_data_set_created_from), parkings.length, date));

                setListen();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                binding.tvDataset.setText(e.toString());
            }
        };
    }

    @Override
    public void showDownloadFailed() {
        binding.tvDataset.setText(R.string.download_failed);
    }

    @Override
    public void showDataConversionFailed() {
        binding.tvDataset.setText(R.string.download_failed);
    }

    private String convertLongToTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        return format.format(date);
    }

    private void setListen() {
        binding.tvMap.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ParkingMapActivity.class);
            startActivity(i);
        });

        binding.tvList.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ParkingFuzzySearchActivity.class);
            startActivity(i);
        });

        binding.tvLove.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ParkingListActivity.class);
            i.putExtra("isLove", true);
            startActivity(i);
        });

        binding.tvHistory.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ParkingListActivity.class);
            i.putExtra("isLove", false);
            startActivity(i);
        });
    }
}