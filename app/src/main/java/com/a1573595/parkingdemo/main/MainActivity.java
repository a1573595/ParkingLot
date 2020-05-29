package com.a1573595.parkingdemo.main;

import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.a1573595.parkingdemo.databinding.ActivityMainBinding;
import com.a1573595.parkingdemo.model.DataManager;
import com.a1573595.parkingdemo.parkMap.ParkingMapActivity;
import com.a1573595.parkingdemo.R;
import com.a1573595.parkingdemo.model.data.Park;
import com.a1573595.parkingdemo.parkFuzzySearch.ParkFuzzySearchActivity;
import com.a1573595.parkingdemo.parkList.ParkListActivity;
import com.a1573595.parkingdemo.update.UpdateActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.observers.DisposableSingleObserver;

public class MainActivity extends AppCompatActivity implements MainContract.View {
    private MainPresenter presenter = new MainPresenter(this);

    private static final int REQUEST_LOCATION = 2;

    private ActivityMainBinding binding;

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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION:
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
    public void transitionToUpdate() {
        Pair<View, String> p1 = Pair.create(binding.imageView, "imageView");
        Pair<View, String> p2 = Pair.create(binding.textView, "textView");
        Pair<View, String> p3 = Pair.create(binding.tvDataset, "tv_dataset");

        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(MainActivity.this, p1, p2, p3);

        Intent intent = new Intent(this, UpdateActivity.class);
        startActivity(intent, options.toBundle());
    }

    @Override
    public DisposableSingleObserver<Park[]> showDataSetInfo() {
        return new DisposableSingleObserver<Park[]>() {
            @Override
            public void onSuccess(Park[] parks) {
                String date = convertLongToTime(DataManager.getInstance().sp.readUpdateTime());
                binding.tvDataset.setText(String.format(getString(R.string.total_data_set_created_from), parks.length, date));

                setListen();
            }

            @Override
            public void onError(Throwable e) {
                binding.tvDataset.setText(e.toString());
            }
        };
    }

    private String convertLongToTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return format.format(date);
    }

    private void setListen() {
        binding.tvMap.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ParkingMapActivity.class);
            startActivity(i);
        });

        binding.tvList.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ParkFuzzySearchActivity.class);
            startActivity(i);
        });

        binding.tvLove.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ParkListActivity.class);
            i.putExtra("isLove", true);
            startActivity(i);
        });

        binding.tvHistory.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ParkListActivity.class);
            i.putExtra("isLove", false);
            startActivity(i);
        });
    }
}
