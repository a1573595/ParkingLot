package com.a1573595.parkinglotdemo.page.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.activity.viewModels
import com.a1573595.parkinglotdemo.BaseActivity
import com.a1573595.parkinglotdemo.R
import com.a1573595.parkinglotdemo.databinding.ActivityDetailBinding

class DetailActivity : BaseActivity() {
    companion object {
        private const val KEY_ID = "id"

        fun startActivity(context: Context, id: String) {
            val intent = Intent(context, DetailActivity::class.java)
            val bundle = Bundle()
            bundle.putString(KEY_ID, id)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    private val viewModel: DetailViewModel by viewModels()

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        subscriptViewModel()
        viewModel.loadData(intent.extras?.getString(KEY_ID))
    }

    private fun subscriptViewModel() {
        viewModel.parkingLotEvent.observe(this) {
            val parkingLot = it.peekContent()
            supportActionBar?.title = parkingLot.name

            binding.tvName.text = parkingLot.name
            binding.tvAddress.text = parkingLot.address
            binding.tvArea.text = parkingLot.area
            binding.tvPhone.text = parkingLot.tel
            binding.tvInfo.text = parkingLot.summary
            binding.tvRate.text = parkingLot.payex
            binding.tvBus.text = java.lang.String.valueOf(parkingLot.totalbus)
            binding.tvCar.text = java.lang.String.valueOf(parkingLot.totalcar)
            binding.tvMoto.text = java.lang.String.valueOf(parkingLot.totalmotor)
            binding.tvBike.text = java.lang.String.valueOf(parkingLot.totalbike)

            binding.imgFavorite.setOnClickListener {
                viewModel.addFavorite(parkingLot.id)
            }
        }

        viewModel.isFavoriteEvent.observeForever {
            binding.imgFavorite.setImageResource(if (it.peekContent()) R.drawable.ic_favorite else R.drawable.ic_unfavorite)

            val scaleAnimation = ScaleAnimation(
                1.0f, 1.2f, 1.0f, 1.2f,
                Animation.RELATIVE_TO_SELF, .5f,
                Animation.RELATIVE_TO_SELF, .5f
            )
            scaleAnimation.duration = 300

            binding.imgFavorite.startAnimation(scaleAnimation)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}