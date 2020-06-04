package com.a1573595.parkingdemo.update;

import android.os.Bundle;
import android.transition.ChangeBounds;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;

import com.a1573595.parkingdemo.BaseActivity;
import com.a1573595.parkingdemo.databinding.ActivityUpdateBinding;

public class UpdateActivity extends BaseActivity implements UpdateContract.View {
    private UpdatePresenter presenter;

    private ActivityUpdateBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupWindowAnimations();

        presenter.downloadDataSet();
    }

    @Override
    protected void createPresenter() {
        presenter = ViewModelProviders.of(this).get(UpdatePresenter.class);
        presenter.setView(this);
    }

    private void setupWindowAnimations() {
        ChangeBounds enterTransition = new ChangeBounds();
        enterTransition.setDuration(1000);
        enterTransition.setInterpolator(new DecelerateInterpolator());
        getWindow().setSharedElementEnterTransition(enterTransition);
    }

    @Override
    public void updateFinished() {
        onBackPressed();
    }

    @Override
    public void updateFailed(String msg) {
        runOnUiThread(() -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());
    }
}
