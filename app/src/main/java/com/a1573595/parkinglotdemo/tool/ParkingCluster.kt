package com.a1573595.parkinglotdemo.tool

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class ParkingCluster(
    private val mPosition: LatLng,
    val id: String,
    val name: String?,
    val area: String?,
    val totalCar: Int,
    val totalMotor: Int,
    val totalBike: Int,
    val totalBus: Int
) : ClusterItem {
    override fun getPosition(): LatLng = mPosition

    override fun getTitle(): String? = null

    override fun getSnippet(): String? = null

    override fun getZIndex(): Float? = null
}