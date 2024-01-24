package com.a1573595.parkinglotdemo.page.fuzzySearch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import com.a1573595.parkinglotdemo.BaseActivity
import com.a1573595.parkinglotdemo.R
import com.a1573595.parkinglotdemo.databinding.ActivityFuzzySearchBinding

class FuzzySearchActivity : BaseActivity() {
    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, FuzzySearchActivity::class.java))
        }
    }

    private val viewModel: FuzzySearchViewModel by viewModels()

    private lateinit var binding: ActivityFuzzySearchBinding

    private val adapter: FuzzySearchAdapter = FuzzySearchAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFuzzySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.recyclerView.adapter = adapter

        binding.edSearch.addTextChangedListener {
            viewModel.setKeyword(binding.edSearch.text.toString())
        }

        binding.groupTransportation.setOnCheckedStateChangeListener { _, checkedIds ->
            val mode = when (checkedIds.firstOrNull()) {
                R.id.chip_bus -> 0
                R.id.chip_car -> 1
                R.id.chip_moto -> 2
                R.id.chip_bike -> 3
                else -> 1
            }

            viewModel.setMode(mode)
        }

        subscriptViewModel()
        viewModel.loadDataSet()
    }

    private fun subscriptViewModel() {
        viewModel.dataSetEvent.observe(this) {
            adapter.submitList(it.peekContent())
            binding.recyclerView.postDelayed({
                binding.recyclerView.layoutManager?.scrollToPosition(0)
            }, 200)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}