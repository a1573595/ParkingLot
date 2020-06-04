package com.a1573595.parkingdemo.parkInfo;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import androidx.lifecycle.ViewModelProviders;

import com.a1573595.parkingdemo.BaseActivity;
import com.a1573595.parkingdemo.R;
import com.a1573595.parkingdemo.databinding.ActivityParkInfoBinding;
import com.a1573595.parkingdemo.model.data.Love;
import com.a1573595.parkingdemo.model.data.Park;

import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;

public class ParkInfoActivity extends BaseActivity implements ParkInfoContract.View {
    private ParkInfoPresenter presenter;

    private ActivityParkInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityParkInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        presenter.readParkData();
        presenter.readLoveData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void createPresenter() {
        presenter = ViewModelProviders.of(this).get(ParkInfoPresenter.class);
        presenter.setView(this);
        presenter.setID(getIntent().getExtras().getString("id"));
    }

    @Override
    public DisposableSingleObserver<Park> showParkInfo() {
        return new DisposableSingleObserver<Park>() {
            @Override
            public void onSuccess(Park park) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(park.name);
                }

                binding.tvName.setText(park.name);
                binding.tvAddress.setText(park.address);
                binding.tvArea.setText(park.area);
                binding.tvPhone.setText(park.tel);
                binding.tvSummary.setText(park.summary);
                binding.tvPayInfo.setText(park.payex);
                binding.tvBus.setText(String.valueOf(park.totalbus));
                binding.tvCar.setText(String.valueOf(park.totalcar));
                binding.tvMoto.setText(String.valueOf(park.totalmotor));
                binding.tvBike.setText(String.valueOf(park.totalbike));

                presenter.addHistory();

                setListen();
            }

            @Override
            public void onError(Throwable e) {
            }
        };
    }

    @Override
    public DisposableSingleObserver<Love> showLove() {
        return new DisposableSingleObserver<Love>() {
            @Override
            public void onSuccess(Love love) {
                binding.imgLove.setImageResource(R.drawable.love);
            }

            @Override
            public void onError(Throwable e) {
            }
        };
    }

    @Override
    public DisposableCompletableObserver changeLove(boolean isLove) {
        return new DisposableCompletableObserver() {
            @Override
            public void onComplete() {
                binding.imgLove.setImageResource(isLove ? R.drawable.love : R.drawable.unlove);

                ScaleAnimation scaleAnimation = new ScaleAnimation(
                        1.0f, 1.2f, 1.0f, 1.2f,
                        Animation.RELATIVE_TO_SELF, 1f,
                        Animation.RELATIVE_TO_SELF, 1f);
                scaleAnimation.setDuration(300);

                binding.imgLove.startAnimation(scaleAnimation);
            }

            @Override
            public void onError(Throwable e) {
            }
        };
    }

    private void setListen() {
        binding.imgLove.setOnClickListener(view -> {
            if (binding.imgLove.getDrawable().getConstantState().equals(
                    getResources().getDrawable(R.drawable.love).getConstantState())) {
                presenter.writeLove(false);
            } else {
                presenter.writeLove(true);
            }
        });
    }
}
