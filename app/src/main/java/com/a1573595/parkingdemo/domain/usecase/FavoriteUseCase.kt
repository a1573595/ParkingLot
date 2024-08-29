package com.a1573595.parkingdemo.domain.usecase

import com.a1573595.parkingdemo.data.local.Favorite
import com.a1573595.parkingdemo.domain.model.ParkingLot
import com.a1573595.parkingdemo.domain.repository.ParkingLotRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class FavoriteUseCase @Inject constructor(
    private val parkingLotRepository: ParkingLotRepository,
) {
    operator fun invoke(): Flow<List<ParkingLot>> =
        parkingLotRepository.favoriteParkingLotListFlow.flowOn(Dispatchers.IO)

    fun getById(id: String): Flow<Favorite?> = parkingLotRepository.getFavoriteByIdFlow(id).flowOn(Dispatchers.IO)

    suspend fun upsertById(id: String): Unit = parkingLotRepository.upsertFavoriteById(id)

    suspend fun deleteById(id: String): Unit = parkingLotRepository.deleteFavoriteById(id)
}