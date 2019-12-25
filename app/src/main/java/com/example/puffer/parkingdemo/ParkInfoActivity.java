package com.example.puffer.parkingdemo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.puffer.parkingdemo.model.DataManager;
import com.example.puffer.parkingdemo.model.History;
import com.example.puffer.parkingdemo.model.Love;
import com.example.puffer.parkingdemo.model.Park;

public class ParkInfoActivity extends AppCompatActivity {
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

        readData();

        setListen();
    }

    private void findView(){
        love_image = findViewById(R.id.img_love);

        tv_name = findViewById(R.id.tv_name);
        tv_phone = findViewById(R.id.tv_phone);
        tv_area = findViewById(R.id.tv_area);
        tv_address = findViewById(R.id.tv_address);
        tv_payInfo = findViewById(R.id.tv_payInfo);
        tv_summary = findViewById(R.id.tv_summary);
        tv_bus = findViewById(R.id.tv_car);
        tv_car = findViewById(R.id.tv_car);
        tv_moto = findViewById(R.id.tv_moto);
        tv_bike = findViewById(R.id.tv_bike);
    }

    private void readData(){
        Park park = DataManager.getInstance().getParkDao().getByID(id);
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

        if(DataManager.getInstance().getLoveDao().getByID(id) != null)
            love_image.setImageResource(R.drawable.love);

        DataManager.getInstance().getHistoryDao().insert(new History(id, System.currentTimeMillis()));
    }

    public void setListen(){
        love_image.setOnClickListener(view -> {
            if(love_image.getDrawable().getConstantState().equals(
                    getResources().getDrawable(R.drawable.love).getConstantState())) {
                DataManager.getInstance().getLoveDao().deleteByID(id);
                love_image.setImageResource(R.drawable.love2);
            }else{
                DataManager.getInstance().getLoveDao().insert(new Love(id));
                love_image.setImageResource(R.drawable.love);
            }
        });
    }
}
