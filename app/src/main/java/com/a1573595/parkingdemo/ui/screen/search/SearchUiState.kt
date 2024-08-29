package com.a1573595.parkingdemo.ui.screen.search

import androidx.paging.PagingData
import com.a1573595.parkingdemo.domain.model.ParkingLot
import kotlinx.coroutines.flow.Flow

data class SearchUiState(
    val keyword: String,
    val hasBus: Boolean = false,
    val hasCar: Boolean = false,
    val hasMotor: Boolean = false,
    val hasBike: Boolean = false,
    val parkingLotPagingDataFlow: Flow<PagingData<ParkingLot>>,
)