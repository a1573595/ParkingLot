package com.a1573595.parkingdemo.parkFuzzySearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.a1573595.parkingdemo.R;
import com.a1573595.parkingdemo.databinding.ActivityParkFuzzySearchBinding;
import com.a1573595.parkingdemo.model.data.Park;
import com.a1573595.parkingdemo.parkInfo.ParkInfoActivity;
import com.a1573595.parkingdemo.parkList.ParkListAdapter;
import com.a1573595.parkingdemo.parkList.ParkListAdapterContract;
import com.a1573595.parkingdemo.parkList.ParkListAdapterPresenter;

import io.reactivex.observers.DisposableSingleObserver;

public class ParkFuzzySearchActivity extends AppCompatActivity implements ParkFuzzySearchContract.View,
        ParkListAdapterContract.View {
    private ParkFuzzySearchPresenter presenter;
    private ParkListAdapterPresenter adapterPresenter = new ParkListAdapterPresenter(this);

    private ParkListAdapter adapter;

    private ActivityParkFuzzySearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityParkFuzzySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.park_list);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        presenter = new ParkFuzzySearchPresenter(this);

        initList();

        presenter.readParksData(binding.edSearch.getText().toString());

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
                binding.recyclerView.scheduleLayoutAnimation();
            }

            @Override
            public void onError(Throwable e) {
            }
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
    public void itemRemoved(String id) {
    }

    @Override
    public void itemInsert(String id) {
    }

    private void initList() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ParkListAdapter(adapterPresenter);
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
