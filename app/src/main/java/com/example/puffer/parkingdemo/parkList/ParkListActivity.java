package com.example.puffer.parkingdemo.parkList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.example.puffer.parkingdemo.R;
import com.example.puffer.parkingdemo.model.Park;
import com.example.puffer.parkingdemo.parkInfo.ParkInfoActivity;
import com.google.android.material.snackbar.Snackbar;

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
            getSupportActionBar().setTitle(
                    isLove? getString(R.string.favorite_list) : getString(R.string.history_list));
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
    public void itemRemoved(String id) {
        presenter.removeParkData(id);
    }

    @Override
    public void itemInsert(String id) {
        presenter.insertParkData(id);
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

    private ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP | ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = 0;
            int swipeFlags = ItemTouchHelper.LEFT;

            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            Drawable icon = getDrawable(R.drawable.delete);
            icon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            Drawable background = new ColorDrawable(getResources().getColor(android.R.color.holo_red_light));

            View itemView = viewHolder.itemView;
            float multiple = 1.5f;

            int iconMargin = ((itemView.getHeight() - icon.getIntrinsicHeight()) / 2);
            int iconTop = itemView.getTop() + (int)(iconMargin * multiple);
            int iconBottom = iconTop + (int)(icon.getIntrinsicHeight() / multiple);

            if(dX > 0) {    // left
                int iconLeft = itemView.getLeft() + iconMargin;
                int iconRight = iconLeft + (int)(icon.getIntrinsicWidth() / multiple);

                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                background.setBounds(itemView.getLeft(), itemView.getTop(),
                        itemView.getLeft() + (int)dX, itemView.getBottom());
            } else {    // right
                int iconRight = itemView.getRight() - iconMargin;
                int iconLeft = iconRight - (int)(icon.getIntrinsicWidth() / multiple);

                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                background.setBounds(itemView.getRight() + (int)dX, itemView.getTop(),
                        itemView.getRight(), itemView.getBottom());
            }

            background.draw(c);
            icon.draw(c);
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

            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.delete, Snackbar.LENGTH_LONG);

            snackbar.setAction(R.string.recover, view -> {
                adapterPresenter.undoDelete();
                adapter.notifyItemInserted(position);
            });
            snackbar.show();
        }
    };
}
