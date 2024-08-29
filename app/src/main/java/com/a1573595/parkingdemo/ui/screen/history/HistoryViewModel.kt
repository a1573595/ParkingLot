package com.a1573595.parkingdemo.ui.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a1573595.parkingdemo.domain.model.ParkingLot
import com.a1573595.parkingdemo.domain.usecase.HistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyUseCase: HistoryUseCase,
) : ViewModel() {
    val parkingLotListFlow: Flow<List<ParkingLot>> = historyUseCase()

    fun deleteById(id: String) = viewModelScope.launch {
        historyUseCase.deleteById(id)
    }
}