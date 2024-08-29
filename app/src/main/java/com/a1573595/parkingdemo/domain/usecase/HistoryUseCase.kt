package com.a1573595.parkingdemo.domain.usecase

import com.a1573595.parkingdemo.domain.model.ParkingLot
import com.a1573595.parkingdemo.domain.repository.ParkingLotRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class HistoryUseCase @Inject constructor(
    private val parkingLotRepository: ParkingLotRepository,
) {
    operator fun invoke(): Flow<List<ParkingLot>> =
        parkingLotRepository.historyParkingLotListFlow.flowOn(Dispatchers.IO)

    suspend fun deleteById(id: String): Unit = parkingLotRepository.deleteHistoryById(id)
}