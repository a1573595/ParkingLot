package com.example.puffer.parkingdemo.update;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.transition.ChangeBounds;
import android.view.animation.DecelerateInterpolator;

import com.example.puffer.parkingdemo.databinding.ActivityUpdateBinding;

public class UpdateActivity extends AppCompatActivity implements UpdateContract.View{
    private UpdatePresenter presenter = new UpdatePresenter(this);

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
        onBackPressed();
    }

    @Override
    public void updateFailed(String msg) {
        binding.tvDataset.setText(msg);
    }
}
