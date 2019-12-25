package com.example.puffer.parkingdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.example.puffer.parkingdemo.model.DataManager;
import com.example.puffer.parkingdemo.model.Park;

import java.util.ArrayList;
import java.util.Collections;

public class ParkListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    private ArrayList<Park> parkArrayList = new ArrayList<>();
    private ParkListAdapter adapter;

    private int mode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_list);

        mode = getIntent().getIntExtra("mode",0);

        findView();
        initList();
        setListen();
    }

    @Override
    public void onStart(){
        super.onStart();

        readDataSet();
    }

    private void findView(){
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ParkListAdapter(parkArrayList);
        recyclerView.setAdapter(adapter);

        LayoutAnimationController controller = new LayoutAnimationController(
                AnimationUtils.loadAnimation(this, R.anim.grow_fade_in_from_bottom));
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        controller.setDelay(0.3f);
        recyclerView.setLayoutAnimation(controller);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP | ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = 0;
            int swipeFlags = ItemTouchHelper.LEFT;

            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {


            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            if(mode == 1)
                DataManager.getInstance().getHistoryDao().deleteByID(parkArrayList.get(position).id);
            else
                DataManager.getInstance().getLoveDao().deleteByID(parkArrayList.get(position).id);
            parkArrayList.remove(position);

            adapter.notifyItemRemoved(position);
        }
    };

    private void readDataSet() {
        Park[] parks;

        if(mode == 1)
            parks = DataManager.getInstance().getHistoryDao().getHistoryList();
        else
            parks = DataManager.getInstance().getLoveDao().getLoveList();

        parkArrayList.clear();
        Collections.addAll(parkArrayList, parks);
        adapter.notifyDataSetChanged();
    }

    private void setListen(){
        adapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(this, ParkInfoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", parkArrayList.get(position).id);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }
}
