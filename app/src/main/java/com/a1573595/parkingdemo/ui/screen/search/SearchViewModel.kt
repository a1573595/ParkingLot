package com.a1573595.parkingdemo.ui.screen.search

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.a1573595.parkingdemo.domain.usecase.ParkingLotUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val parkingLotUseCase: ParkingLotUseCase,
) : ViewModel() {
    private val _uiState = mutableStateOf(
        SearchUiState(
            keyword = "",
            parkingLotPagingDataFlow = parkingLotUseCase.searchPagingDataFlow().cachedIn(viewModelScope)
        )
    )

    val uiState: State<SearchUiState> = _uiState

    fun updateKeyword(value: String) {
        _uiState.value = uiState.value.copy(keyword = value)

        updateFlow()
    }

    fun updateFilter(filterType: FilterType) {
        when (filterType) {
            FilterType.BUS -> _uiState.value = uiState.value.copy(hasBus = !uiState.value.hasBus)
            FilterType.CAR -> _uiState.value = uiState.value.copy(hasCar = !uiState.value.hasCar)
            FilterType.MOTOR -> _uiState.value = uiState.value.copy(hasMotor = !uiState.value.hasMotor)
            FilterType.BIKE -> _uiState.value = uiState.value.copy(hasBike = !uiState.value.hasBike)
        }

        updateFlow()
    }

    @OptIn(FlowPreview::class)
    private fun updateFlow() {
        val state = uiState.value

        parkingLotUseCase.searchPagingDataFlow(
            state.keyword,
            state.hasBus,
            state.hasCar,
            state.hasMotor,
            state.hasBike,
        ).debounce(1000).cachedIn(viewModelScope).let {
            _uiState.value = uiState.value.copy(parkingLotPagingDataFlow = it)
        }
    }
}