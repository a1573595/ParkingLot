package com.a1573595.parkingdemo.domain.usecase

import androidx.paging.PagingData
import com.a1573595.parkingdemo.common.dateFormatIso8601
import com.a1573595.parkingdemo.domain.model.ParkingLot
import com.a1573595.parkingdemo.domain.repository.ParkingLotRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class ParkingLotUseCase @Inject constructor(
    private val parkingLotRepository: ParkingLotRepository,
) {
    val lastUpdateTimeFlow: Flow<String?>
        get() = parkingLotRepository.lastUpdateTimeFlow.map {
            it?.let { value ->
                dateFormatIso8601.format(Date(value))
            }
        }.flowOn(Dispatchers.IO)

    operator fun invoke(): Flow<List<ParkingLot>> = parkingLotRepository.parkingLotListFlow.flowOn(Dispatchers.IO)

    suspend fun fetchDataSet(): Unit = parkingLotRepository.fetchParkingLotDataSet()

    fun searchPagingDataFlow(
        keyword: String = "",
        hasBus: Boolean = false,
        hasCar: Boolean = false,
        hasMotor: Boolean = false,
        hasBike: Boolean = false,
    ): Flow<PagingData<ParkingLot>> =
        parkingLotRepository.searchParkingLotPagingDataFlow(keyword, hasBus, hasCar, hasMotor, hasBike)
            .flowOn(Dispatchers.IO)

    suspend fun getParkingLotById(id: String): ParkingLot? = parkingLotRepository.getParkingLotById(id)
}