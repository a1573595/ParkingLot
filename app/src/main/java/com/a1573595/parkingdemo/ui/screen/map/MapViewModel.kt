package com.a1573595.parkingdemo.ui.screen.map

import androidx.lifecycle.ViewModel
import com.a1573595.parkingdemo.domain.usecase.ParkingLotUseCase
import com.a1573595.parkingdemo.ui.screen.map.bean.ParkingLotCluster
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val parkingLotUseCase: ParkingLotUseCase,
) : ViewModel() {
    val parkingLotClusterListFlow: Flow<List<ParkingLotCluster>> = parkingLotUseCase().map {
        it.map {  ParkingLotCluster.fromParkingLot(it) }
    }
}