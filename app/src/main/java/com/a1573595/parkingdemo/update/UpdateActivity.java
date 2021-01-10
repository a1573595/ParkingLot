package com.a1573595.parkingdemo.update;

import android.os.Bundle;
import android.transition.ChangeBounds;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.a1573595.parkingdemo.BaseActivity;
import com.a1573595.parkingdemo.R;
import com.a1573595.parkingdemo.databinding.ActivityUpdateBinding;

public class UpdateActivity extends BaseActivity<UpdatePresenter> implements UpdateView {
    private ActivityUpdateBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupWindowAnimations();

        presenter.downloadDataSet();
    }

    private void setupWindowAnimations() {
        ChangeBounds enterTransition = new ChangeBounds();
        enterTransition.setDuration(1000);
        enterTransition.setInterpolator(new DecelerateInterpolator());
        getWindow().setSharedElementEnterTransition(enterTransition);
    }

    @Override
    public void updateFinished() {
        binding.tvDataset.setText(getString(R.string.update_finish));
        onBackPressed();
    }

    @Override
    public void updateFailed(String msg) {
        runOnUiThread(() -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());
    }
}