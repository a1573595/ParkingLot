package com.a1573595.parkingdemo.parkingFuzzySearch;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.a1573595.parkingdemo.BaseActivity;
import com.a1573595.parkingdemo.R;
import com.a1573595.parkingdemo.databinding.ActivityParkingFuzzySearchBinding;
import com.a1573595.parkingdemo.parkingInfo.ParkingInfoActivity;

public class ParkingFuzzySearchActivity extends BaseActivity implements ParkingFuzzySearchContract.View {
    private ParkingFuzzySearchPresenter presenter;

    private ActivityParkingFuzzySearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityParkingFuzzySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.parking_list);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initList();

        presenter.readParksData(binding.edSearch.getText().toString());

        setListen();
    }

    @Override
    protected void createPresenter() {
        presenter = ViewModelProviders.of(this).get(ParkingFuzzySearchPresenter.class);
        presenter.setView(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void showLayoutAnimation() {
        binding.recyclerView.scheduleLayoutAnimation();
    }

    @Override
    public void onItemClick(String id) {
        Intent intent = new Intent(this, ParkingInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void initList() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ParkingFuzzySearchAdapter adapter = new ParkingFuzzySearchAdapter(presenter);
        presenter.setAdapter(adapter);
        binding.recyclerView.setAdapter(adapter);

        LayoutAnimationController controller = new LayoutAnimationController(
                AnimationUtils.loadAnimation(this, R.anim.grow_fade_in_from_bottom));
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        controller.setDelay(0.3f);
        binding.recyclerView.setLayoutAnimation(controller);
    }

    private void setListen() {
        binding.edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.readParksData(binding.edSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.groupTransportation.setOnCheckedChangeListener((group, checkedId) -> {
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

            presenter.readParksData(binding.edSearch.getText().toString());
        });
    }
}
