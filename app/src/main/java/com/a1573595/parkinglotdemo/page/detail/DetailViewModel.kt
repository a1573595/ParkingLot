package com.a1573595.parkinglotdemo.page.detail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.a1573595.parkinglotdemo.BaseViewModel
import com.a1573595.parkinglotdemo.database.ParkingLot
import com.a1573595.parkinglotdemo.repository.ParkingLotRepository
import com.a1573595.parkinglotdemo.tool.Event

class DetailViewModel : BaseViewModel {
    constructor(application: Application) : super(application) {
        repository = ParkingLotRepository()
    }

    constructor(application: Application, repository: ParkingLotRepository) : super(application) {
        this.repository = repository
    }

    private val repository: ParkingLotRepository

    private var id: String = ""

    val parkingLotEvent: MutableLiveData<Event<ParkingLot>> = MutableLiveData()
    val isFavoriteEvent: MutableLiveData<Event<Boolean>> = MutableLiveData()

    fun loadData(id: String?) {
        id?.let {
            this.id = it
        }

        addDisposable(repository.getFavorites(this.id).subscribe({
            isFavoriteEvent.postValue(Event(true))
        }, {
            isFavoriteEvent.postValue(Event(false))
        }))

        addDisposable(repository.getParkingLot(this.id)
            .subscribe { list ->
                parkingLotEvent.postValue(Event(list))
            }
        )
    }

    fun addFavorite(id: String) {
        if (isFavoriteEvent.value?.peekContent() == true) {
            addDisposable(repository.deleteFavorite(id).subscribe {
                isFavoriteEvent.postValue(Event(false))
            })
        } else {
            addDisposable(repository.addFavorite(id).subscribe {
                isFavoriteEvent.postValue(Event(true))
            })
        }
    }
}