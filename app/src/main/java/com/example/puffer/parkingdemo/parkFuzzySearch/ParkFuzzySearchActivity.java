package com.example.puffer.parkingdemo.parkFuzzySearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.example.puffer.parkingdemo.R;
import com.example.puffer.parkingdemo.model.Park;
import com.example.puffer.parkingdemo.parkInfo.ParkInfoActivity;
import com.example.puffer.parkingdemo.parkList.ParkListAdapter;
import com.example.puffer.parkingdemo.parkList.ParkListAdapterContract;
import com.example.puffer.parkingdemo.parkList.ParkListAdapterPresenter;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import io.reactivex.observers.DisposableSingleObserver;

public class ParkFuzzySearchActivity extends AppCompatActivity implements ParkFuzzySearchContract.View,
        ParkListAdapterContract.View {
    private ParkFuzzySearchPresenter presenter;
    private ParkListAdapterPresenter adapterPresenter = new ParkListAdapterPresenter(this);

    private TextInputEditText ed_search;
    private ChipGroup group_transportation;

    private RecyclerView recyclerView;
    private ParkListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_fuzzy_search);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("停車場列表");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        presenter = new ParkFuzzySearchPresenter(this);

        findView();
        initList();

        presenter.readParksData(ed_search.getText().toString());

        setListen();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public DisposableSingleObserver<Park[]> showParkList() {
        return new DisposableSingleObserver<Park[]>() {
            @Override
            public void onSuccess(Park[] parks) {
                adapterPresenter.loadData(parks);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) { }
        };
    }

    @Override
    public void onItemClick(String id) {
        Intent intent = new Intent(this, ParkInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void itemRemoved(String id) { }

    @Override
    public void itemInsert(String id) { }

    private void findView(){
        ed_search = findViewById(R.id.ed_search);
        group_transportation = findViewById(R.id.group_transportation);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void initList(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ParkListAdapter(adapterPresenter);
        recyclerView.setAdapter(adapter);

        LayoutAnimationController controller = new LayoutAnimationController(
                AnimationUtils.loadAnimation(this, R.anim.grow_fade_in_from_bottom));
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        controller.setDelay(0.3f);
        recyclerView.setLayoutAnimation(controller);
    }

    private void setListen(){
        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.readParksData(ed_search.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        group_transportation.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.chip_bus:
                    presenter.setMode(0);
                    break;
                case R.id.chip_car:
                    presenter.setMode(1);
                    break;
                case R.id.chip_moto:
                    presenter.setMode(2);
                    break;
                case R.id.chip_bike:
                    presenter.setMode(3);
                    break;
            }

            presenter.readParksData(ed_search.getText().toString());
        });
    }
}
