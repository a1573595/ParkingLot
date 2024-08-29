package com.a1573595.parkingdemo.domain.repository

import androidx.paging.PagingData
import com.a1573595.parkingdemo.data.local.Favorite
import com.a1573595.parkingdemo.domain.model.ParkingLot
import kotlinx.coroutines.flow.Flow

interface ParkingLotRepository {
    val lastUpdateTimeFlow: Flow<Long?>

    val parkingLotListFlow: Flow<List<ParkingLot>>

    val favoriteParkingLotListFlow: Flow<List<ParkingLot>>

    val historyParkingLotListFlow: Flow<List<ParkingLot>>

    suspend fun fetchParkingLotDataSet(): Unit

    fun searchParkingLotPagingDataFlow(
        keyword: String,
        hasBus: Boolean,
        hasCar: Boolean,
        hasMotor: Boolean,
        hasBike: Boolean,
    ): Flow<PagingData<ParkingLot>>

    suspend fun getParkingLotById(id: String): ParkingLot?

    fun getFavoriteByIdFlow(id: String): Flow<Favorite?>

    suspend fun upsertFavoriteById(id: String): Unit

    suspend fun deleteFavoriteById(id: String): Unit

    suspend fun deleteHistoryById(id: String): Unit
}