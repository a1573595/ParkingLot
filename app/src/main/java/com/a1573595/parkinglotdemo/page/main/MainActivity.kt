package com.a1573595.parkinglotdemo.page.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.viewModels
import com.a1573595.parkinglotdemo.BaseActivity
import com.a1573595.parkinglotdemo.R
import com.a1573595.parkinglotdemo.databinding.ActivityMainBinding
import com.a1573595.parkinglotdemo.page.history.HistoryActivity
import com.a1573595.parkinglotdemo.page.fuzzySearch.FuzzySearchActivity
import com.a1573595.parkinglotdemo.page.map.MapActivity

class MainActivity : BaseActivity() {
    private val viewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    private val backHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerOnBackPress()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        subscriptViewModel()
        viewModel.loadDataSet()
    }

    private fun subscriptViewModel() {
        viewModel.dataSetEvent.observe(this) {
            binding.tvDataset.text = getString(R.string.total_of_data_set, it.peekContent().size)

            setListen()
        }

        viewModel.updateTimeEvent.observe(this) {
            binding.tvUpdateTime.text = getString(R.string.download_at, it.peekContent())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_update, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_update -> {
                binding.tvDataset.text = getString(R.string.downloading)
                viewModel.updateDataSet()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun registerOnBackPress() {
        onBackPressedDispatcher.addCallback {
            if (backHandler.hasMessages(0)) {
                finish()
            } else {
                Toast.makeText(this@MainActivity, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT)
                    .show()
                backHandler.removeCallbacksAndMessages(null)
                backHandler.postDelayed({}, 2000)
            }
        }
    }

    private fun setListen() {
        binding.cardMap.setOnClickListener {
            MapActivity.startActivity(this)
        }

        binding.cardList.setOnClickListener {
            FuzzySearchActivity.startActivity(this)
        }

        binding.cardFavorite.setOnClickListener {
            HistoryActivity.startActivity(this, HistoryActivity.MODE_FAVORITE)
        }

        binding.cardHistory.setOnClickListener {
            HistoryActivity.startActivity(this, HistoryActivity.MODE_HISTORY)
        }
    }
}