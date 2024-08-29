package com.a1573595.parkingdemo.ui.screen.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a1573595.parkingdemo.domain.model.ParkingLot
import com.a1573595.parkingdemo.domain.usecase.FavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteUseCase: FavoriteUseCase,
) : ViewModel() {
    val parkingLotListFlow: Flow<List<ParkingLot>> = favoriteUseCase()

    fun deleteById(id: String) = viewModelScope.launch {
        favoriteUseCase.deleteById(id)
    }
}