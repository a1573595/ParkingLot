package com.a1573595.parkinglotdemo.page.map

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.a1573595.parkinglotdemo.BaseViewModel
import com.a1573595.parkinglotdemo.database.ParkingLot
import com.a1573595.parkinglotdemo.repository.ParkingLotRepository
import com.a1573595.parkinglotdemo.tool.Event

class MapViewModel : BaseViewModel {
    constructor(application: Application) : super(application) {
        repository = ParkingLotRepository()
    }

    constructor(application: Application, repository: ParkingLotRepository) : super(application) {
        this.repository = repository
    }

    private val repository: ParkingLotRepository

    val dataSetEvent: MutableLiveData<Event<List<ParkingLot>>> = MutableLiveData()

    fun loadDataSet() {
        addDisposable(repository.getParkingLots().subscribe { list ->
            dataSetEvent.postValue(Event(list))
        })
    }
}