package com.a1573595.parkinglotdemo.page.fuzzySearch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.a1573595.parkinglotdemo.R
import com.a1573595.parkinglotdemo.database.ParkingLot
import com.a1573595.parkinglotdemo.database.ParkingLotCallback
import com.a1573595.parkinglotdemo.databinding.ItemParkingLotBinding
import com.a1573595.parkinglotdemo.page.detail.DetailActivity

internal class FuzzySearchAdapter :
    ListAdapter<ParkingLot, FuzzySearchAdapter.Holder>(ParkingLotCallback()) {
    internal inner class Holder(private val binding: ItemParkingLotBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                DetailActivity.startActivity(it.context, getItem(adapterPosition).id)
            }
        }

        fun bind(parkingLot: ParkingLot) {
            binding.tvName.text = parkingLot.name
            binding.tvAddress.text = parkingLot.address
            binding.tvTotal.text = parkingLot.address

            binding.tvTotal.text = binding.tvTotal.context.getString(
                R.string.transportation,
                parkingLot.totalbus,
                parkingLot.totalcar,
                parkingLot.totalmotor,
                parkingLot.totalbike
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            ItemParkingLotBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}