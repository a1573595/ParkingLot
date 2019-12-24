package com.example.puffer.parkingdemo;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ParkingInfoActivity extends AppCompatActivity {
    private Activity mActivity;

    private SQLiteDatabase draw;

    private ImageView love_image;
    private TextView name, telephone, area, address, payInfo, summary;
    private TextView car, moto, bike;

    private int _id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_info);

        initDataBase();
        findView();
        setText();


        setListen();
    }

    private void initDataBase(){
        MyDB mydb=new MyDB(this);
        draw = mydb.getWritableDatabase();
    }

    private void findView(){
        love_image = findViewById(R.id.img_love);

        name = findViewById(R.id.tv_name);
        telephone = findViewById(R.id.tv_phone);
        area = findViewById(R.id.tv_area);
        address = findViewById(R.id.tv_address);
        payInfo = findViewById(R.id.tv_payInfo);
        summary = findViewById(R.id.tv_summary);
        car = findViewById(R.id.tv_car);
        moto = findViewById(R.id.tv_moto);
        bike = findViewById(R.id.tv_bike);
    }

    private void setText(){
        String search_text = "SELECT Name,Address,Area,Telephone,Summary,PayInfo,TotalCar,TotalMotor,TotalBike,Info_id FROM Parking_information WHERE Name LIKE \'"+getIntent().getExtras().getString("name")+"\'";
        Log.e("test",search_text);
        Cursor mcursor = draw.rawQuery(search_text, null);
        mcursor.moveToFirst();
        name.setText(mcursor.getString(0));
        address.setText(mcursor.getString(1));
        area.setText(mcursor.getString(2));
        telephone.setText(mcursor.getString(3));
        summary.setText(mcursor.getString(4));
        payInfo.setText(mcursor.getString(5));
        car.setText(String.valueOf(mcursor.getInt(6)));
        moto.setText(String.valueOf(mcursor.getInt(7)));
        bike.setText(String.valueOf(mcursor.getInt(8)));
        _id = mcursor.getInt(9);
        mcursor.close();

        String insert_text = "INSERT OR REPLACE INTO Parking_history(ViewTime,Info_id) VALUES (?, ?)";
        draw.execSQL(insert_text,new Object[]{System.currentTimeMillis(),_id});
    }

    public void setListen(){
        String search_text = "SELECT * FROM Parking_love WHERE Info_id LIKE \'"+_id+"\'";
        Cursor mcursor = draw.rawQuery(search_text, null);
        if(mcursor.getCount()>0)
            love_image.setImageResource(R.drawable.love);

        love_image.setOnClickListener(view -> {
            String exec_text;
            if(love_image.getDrawable().getConstantState().equals(
                    getResources().getDrawable(R.drawable.love).getConstantState())) {
                exec_text = "DELETE FROM Parking_love WHERE Info_id LIKE "+_id;
                love_image.setImageResource(R.drawable.love2);
                draw.execSQL(exec_text);
            }else{
                exec_text = "INSERT OR REPLACE INTO Parking_love(Info_id) VALUES (?)";
                love_image.setImageResource(R.drawable.love);
                draw.execSQL(exec_text, new Object[]{_id});
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        draw.close();
    }
}
