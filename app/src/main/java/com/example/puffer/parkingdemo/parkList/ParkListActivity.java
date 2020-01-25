package com.example.puffer.parkingdemo.parkList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.example.puffer.parkingdemo.R;
import com.example.puffer.parkingdemo.model.Park;
import com.example.puffer.parkingdemo.parkInfo.ParkInfoActivity;

import io.reactivex.observers.DisposableSingleObserver;

public class ParkListActivity extends AppCompatActivity implements ParkListContract.View,
        ParkListAdapterContract.View {
    private ParkListPresenter presenter;
    private ParkListAdapterPresenter adapterPresenter = new ParkListAdapterPresenter(this);

    private RecyclerView recyclerView;
    private ParkListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_list);

        boolean isLove = getIntent().getBooleanExtra("isLove", false);

        setSupportActionBar(findViewById(R.id.toolbar));
        if(getSupportActionBar() != null) {
//            CollapsingToolbarLayout collapsing = findViewById(R.id.collapsing);
//            collapsing.setTitleEnabled(false);

            getSupportActionBar().setTitle(isLove? "最愛列表" : "歷史列表");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        presenter = new ParkListPresenter(this, isLove);

        findView();

        initList();
    }

    @Override
    public void onStart(){
        super.onStart();

        presenter.readParksData();
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
            }

            @Override
            public void onError(Throwable e) { }
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
    public void notifyItemRemoved(String id) {
        presenter.removeParkData(id);
    }

    private void findView(){
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ParkListAdapter(adapterPresenter);
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

            adapterPresenter.removeItem(position);
            adapter.notifyItemRemoved(position);
        }
    };
}
