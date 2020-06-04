package com.a1573595.parkingdemo.parkList;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
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

import com.a1573595.parkingdemo.BaseActivity;
import com.a1573595.parkingdemo.R;
import com.a1573595.parkingdemo.databinding.ActivityParkListBinding;
import com.a1573595.parkingdemo.parkInfo.ParkInfoActivity;
import com.google.android.material.snackbar.Snackbar;

public class ParkListActivity extends BaseActivity implements ParkListContract.View {
    private ParkListPresenter presenter;

    private ActivityParkListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityParkListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(
                    getIntent().getBooleanExtra("isLove", false) ?
                            getString(R.string.favorite_list) : getString(R.string.history_list));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initList();
    }

    @Override
    public void onStart() {
        super.onStart();

        presenter.readParksData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void createPresenter() {
        presenter = ViewModelProviders.of(this).get(ParkListPresenter.class);
        presenter.setView(this);
        presenter.setLove(getIntent().getBooleanExtra("isLove", false));
    }

    @Override
    public void onItemClick(String id) {
        Intent intent = new Intent(this, ParkInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void initList() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ParkListAdapter adapter = new ParkListAdapter(presenter);
        presenter.setAdapter(adapter);
        binding.recyclerView.setAdapter(adapter);

        LayoutAnimationController controller = new LayoutAnimationController(
                AnimationUtils.loadAnimation(this, R.anim.grow_fade_in_from_bottom));
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        controller.setDelay(0.3f);
        binding.recyclerView.setLayoutAnimation(controller);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(binding.recyclerView);
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
            int iconTop = itemView.getTop() + (int) (iconMargin * multiple);
            int iconBottom = iconTop + (int) (icon.getIntrinsicHeight() / multiple);

            if (dX > 0) {    // left
                int iconLeft = itemView.getLeft() + iconMargin;
                int iconRight = iconLeft + (int) (icon.getIntrinsicWidth() / multiple);

                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                background.setBounds(itemView.getLeft(), itemView.getTop(),
                        itemView.getLeft() + (int) dX, itemView.getBottom());
            } else {    // right
                int iconRight = itemView.getRight() - iconMargin;
                int iconLeft = iconRight - (int) (icon.getIntrinsicWidth() / multiple);

                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(),
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

            presenter.removeItem(position);

            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.delete, Snackbar.LENGTH_LONG);

            snackbar.setAction(R.string.recover, view -> {
                presenter.undoDelete();
            });
            snackbar.show();
        }
    };
}
