package com.example.puffer.parkingdemo.parkInfo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.puffer.parkingdemo.R;
import com.example.puffer.parkingdemo.model.Love;
import com.example.puffer.parkingdemo.model.Park;

import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class ParkInfoActivity extends AppCompatActivity implements ParkInfoContract.View {
    private ParkInfoPresenter presenter = new ParkInfoPresenter(this);

    private ImageView love_image;
    private TextView tv_name, tv_phone, tv_area, tv_address, tv_payInfo, tv_summary;
    private TextView tv_bus, tv_car, tv_moto, tv_bike;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_info);

        id = getIntent().getExtras().getString("id");

        findView();

        presenter.readParkData(id);
        presenter.readLoveData(id);
    }

    @Override
    public SingleObserver<Park> showParkInfo() {
        return new SingleObserver<Park>() {
            @Override
            public void onSubscribe(Disposable d) { }

            @Override
            public void onSuccess(Park park) {
                tv_name.setText(park.name);
                tv_address.setText(park.address);
                tv_area.setText(park.area);
                tv_phone.setText(park.tel);
                tv_summary.setText(park.summary);
                tv_payInfo.setText(park.payex);
                tv_bus.setText(String.valueOf(park.totalbus));
                tv_car.setText(String.valueOf(park.totalcar));
                tv_moto.setText(String.valueOf(park.totalmotor));
                tv_bike.setText(String.valueOf(park.totalbike));

                presenter.writeHistory(id);

                setListen();
            }

            @Override
            public void onError(Throwable e) { }
        };
    }

    @Override
    public SingleObserver<Love> showLove() {
        return new SingleObserver<Love>() {
            @Override
            public void onSubscribe(Disposable d) { }

            @Override
            public void onSuccess(Love love) {
                love_image.setImageResource(R.drawable.love);
            }

            @Override
            public void onError(Throwable e) { }
        };
    }

    @Override
    public CompletableObserver changeLove(boolean isLove) {
        return new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) { }

            @Override
            public void onComplete() {
                love_image.setImageResource(isLove? R.drawable.love : R.drawable.love2);
            }

            @Override
            public void onError(Throwable e) { }
        };
    }

    private void findView(){
        love_image = findViewById(R.id.img_love);

        tv_name = findViewById(R.id.tv_name);
        tv_phone = findViewById(R.id.tv_phone);
        tv_area = findViewById(R.id.tv_area);
        tv_address = findViewById(R.id.tv_address);
        tv_payInfo = findViewById(R.id.tv_payInfo);
        tv_summary = findViewById(R.id.tv_summary);
        tv_bus = findViewById(R.id.tv_bus);
        tv_car = findViewById(R.id.tv_car);
        tv_moto = findViewById(R.id.tv_moto);
        tv_bike = findViewById(R.id.tv_bike);
    }

    public void setListen(){
        love_image.setOnClickListener(view -> {
            if(love_image.getDrawable().getConstantState().equals(
                    getResources().getDrawable(R.drawable.love).getConstantState())) {
                presenter.writeLove(id, false);
            }else {
                presenter.writeLove(id, true);
            }
        });
    }
}
