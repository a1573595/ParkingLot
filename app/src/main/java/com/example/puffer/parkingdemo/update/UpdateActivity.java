package com.example.puffer.parkingdemo.update;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.transition.ChangeBounds;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.example.puffer.parkingdemo.R;

public class UpdateActivity extends AppCompatActivity implements UpdateContract.View{
    private UpdatePresenter presenter = new UpdatePresenter(this);

    private TextView tv_dataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        setupWindowAnimations();

        tv_dataset = findViewById(R.id.tv_dataset);

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
        tv_dataset.setText(msg);
    }
}
