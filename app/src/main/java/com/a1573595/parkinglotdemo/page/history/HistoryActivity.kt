package com.a1573595.parkinglotdemo.page.history

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.a1573595.parkinglotdemo.BaseActivity
import com.a1573595.parkinglotdemo.R
import com.a1573595.parkinglotdemo.databinding.ActivityHistoryBinding
import com.google.android.material.snackbar.Snackbar

class HistoryActivity : BaseActivity() {
    companion object {
        private const val KET_MODE = "mode"

        const val MODE_FAVORITE = 1
        const val MODE_HISTORY = 2

        fun startActivity(context: Context, mode: Int) {
            val intent = Intent(context, HistoryActivity::class.java)
            val bundle = Bundle()
            bundle.putInt(KET_MODE, mode)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    private val viewModel: HistoryModel by viewModels()

    private lateinit var binding: ActivityHistoryBinding

    private val adapter: HistoryAdapter = HistoryAdapter()
    private val textPaint = Paint()
    private val backgroundPaint = Paint()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        textPaint.color = ContextCompat.getColor(
            this@HistoryActivity,
            android.R.color.white
        )
        textPaint.textSize = 72f
        textPaint.style = Paint.Style.FILL
        textPaint.textAlign = Paint.Align.CENTER
        backgroundPaint.color = ContextCompat.getColor(
            this@HistoryActivity,
            android.R.color.holo_red_light
        )

        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
        binding.recyclerView.adapter = adapter

        subscriptViewModel()
        intent.extras?.getInt(KET_MODE)?.let {
            viewModel.setMode(it)
        }
    }

    private fun subscriptViewModel() {
        viewModel.dataSetEvent.observe(this, {
            adapter.submitList(it.peekContent())
        })
    }

    override fun onResume() {
        super.onResume()

        viewModel.loadDataSet()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val callback: ItemTouchHelper.Callback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val dragFlags = 0
            val swipeFlags = ItemTouchHelper.LEFT
            return makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            val icon = ContextCompat.getDrawable(this@HistoryActivity, R.drawable.ic_delete)!!
            icon.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    Color.WHITE,
                    BlendModeCompat.SRC_ATOP
                )

            val itemView = viewHolder.itemView

            val rectf: RectF

            val multiple = 1f
            val iconMargin = (itemView.height - icon.intrinsicHeight) / 2
            val iconTop = itemView.top + (iconMargin * multiple).toInt()
            val iconBottom = iconTop + (icon.intrinsicHeight / multiple).toInt()
            if (dX > 0) {   // left
                rectf = RectF(
                    itemView.left.toFloat(),
                    itemView.top.toFloat(),
                    itemView.left + dX,
                    itemView.bottom.toFloat()
                )

                val iconLeft = itemView.left + iconMargin
                val iconRight = iconLeft + (icon.intrinsicWidth / multiple).toInt()
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            } else {    // right
                rectf = RectF(
                    itemView.right + dX,
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat()
                )

                val iconRight = itemView.right - iconMargin
                val iconLeft = iconRight - (icon.intrinsicWidth / multiple).toInt()
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            }

            c.drawRect(rectf, backgroundPaint)
            icon.draw(c)
//            c.drawText(
//                "Delete",
//                rectf.right - (textPaint.textSize * 3), // rectf.centerX()
//                rectf.centerY() + (textPaint.textSize / 2),
//                textPaint
//            )
        }

        override fun onMove(
            recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position: Int = viewHolder.adapterPosition
            viewModel.delete(position)

            Snackbar.make(binding.root, R.string.delete, Snackbar.LENGTH_LONG)
                .setAction(R.string.recover) { viewModel.undoDelete() }
                .show()
        }
    }
}