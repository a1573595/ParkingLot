package com.a1573595.parkinglotdemo.tool

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class ParkingCluster(
    private val mPosition: LatLng,
    var id: String,
    var name: String?,
    var area: String?,
    var totalCar: Int,
    var totalMotor: Int,
    var totalBike: Int,
    var totalBus: Int
) : ClusterItem {
    override fun getPosition(): LatLng {
        return mPosition
    }

    override fun getTitle(): String? {
        return null
    }

    override fun getSnippet(): String? {
        return null
    }
}