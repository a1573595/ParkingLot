package com.a1573595.parkingdemo.ui.screen.detail

import com.a1573595.parkingdemo.domain.model.ParkingLot

data class DetailUiState(
    val isFavorite: Boolean,
    val parkingLot: ParkingLot,
)