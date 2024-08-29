package com.a1573595.parkingdemo.ui.screen.map.bean

import com.a1573595.parkingdemo.domain.model.ParkingLot
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class ParkingLotCluster(
    private val _position: LatLng,
    val id: String,
    val name: String,
    val description: String,
) : ClusterItem {
    companion object {
        fun fromParkingLot(park: ParkingLot): ParkingLotCluster = ParkingLotCluster(
            _position = LatLng(park.lat, park.lon),
            id = park.id,
            name = park.name,
            description = "${park.area}${park.address}",
        )
    }

    override fun getPosition(): LatLng = _position

    override fun getTitle(): String = name

    override fun getSnippet(): String = description

    override fun getZIndex(): Float? = null
}
