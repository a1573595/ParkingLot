package com.a1573595.parkingdemo.ui.screen.detail

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a1573595.parkingdemo.common.AsyncValue
import com.a1573595.parkingdemo.domain.usecase.FavoriteUseCase
import com.a1573595.parkingdemo.domain.usecase.ParkingLotUseCase
import com.a1573595.parkingdemo.ui.navigation.NavRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    private val parkingLotUseCase: ParkingLotUseCase,
    private val favoriteUseCase: FavoriteUseCase,
) : ViewModel() {
    private val id = handle.get<String>(NavRoute.KEY_ID)!!

    private val _uiState: MutableState<AsyncValue<DetailUiState>> = mutableStateOf(AsyncValue.Loading)

    val uiState: State<AsyncValue<DetailUiState>> = _uiState

    init {
        viewModelScope.launch {
            parkingLotUseCase.getParkingLotById(id)!!.let {
                _uiState.value = AsyncValue.Data(DetailUiState(false, it))
            }

            favoriteUseCase.getById(id).collect {
                _uiState.value = AsyncValue.Data(_uiState.value.requireValue.copy(isFavorite = it != null))
            }
        }
    }

    fun updateFavorite() {
        val isFavorite = _uiState.value.requireValue.isFavorite

        viewModelScope.launch {
            if (isFavorite) {
                favoriteUseCase.deleteById(id)
            } else {
                favoriteUseCase.upsertById(id)
            }
        }
    }
}