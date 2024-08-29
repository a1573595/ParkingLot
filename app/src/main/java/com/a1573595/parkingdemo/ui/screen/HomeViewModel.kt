package com.a1573595.parkingdemo.ui.screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a1573595.parkingdemo.common.AsyncValue
import com.a1573595.parkingdemo.domain.usecase.ParkingLotUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val parkingLotUseCase: ParkingLotUseCase,
) : ViewModel() {
    private val _uiState = mutableStateOf<AsyncValue<HomeUiState>>(AsyncValue.Loading)

    val uiState: State<AsyncValue<HomeUiState>> = _uiState

    init {
        viewModelScope.launch {
            parkingLotUseCase.lastUpdateTimeFlow.zip(parkingLotUseCase()) { lastUpdateTime, parkingLotList ->
                if (lastUpdateTime.isNullOrEmpty()) {
                    refreshData()
                } else {
                    _uiState.value = AsyncValue.Data(HomeUiState(lastUpdateTime, parkingLotList.size))
                }
            }.catch {
                _uiState.value = AsyncValue.Error(it)
            }.collect()
        }
    }

    fun refreshData() = viewModelScope.launch {
        _uiState.value = AsyncValue.Loading
        parkingLotUseCase.fetchDataSet()
    }
}