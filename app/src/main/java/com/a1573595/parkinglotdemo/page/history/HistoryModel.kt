package com.a1573595.parkinglotdemo.page.history

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.a1573595.parkinglotdemo.BaseViewModel
import com.a1573595.parkinglotdemo.database.ParkingLot
import com.a1573595.parkinglotdemo.repository.ParkingLotRepository
import com.a1573595.parkinglotdemo.tool.Event

class HistoryModel : BaseViewModel {
    constructor(application: Application) : super(application) {
        repository = ParkingLotRepository()
    }

    constructor(application: Application, repository: ParkingLotRepository) : super(application) {
        this.repository = repository
    }

    private val repository: ParkingLotRepository

    private var mode: Int = 0

    val dataSetEvent: MutableLiveData<Event<List<ParkingLot>>> = MutableLiveData()

    private var lastDeleteID: String? = null

    fun setMode(mode: Int) {
        this.mode = mode
    }

    fun loadDataSet() {
        val completable = if (mode == HistoryActivity.MODE_FAVORITE) {
            repository.getFavorites()
        } else {
            repository.getHistory()
        }

        addDisposable(completable.subscribe { list -> dataSetEvent.postValue(Event(list)) })
    }

    fun delete(position: Int) {
        dataSetEvent.value?.peekContent()?.let {
            lastDeleteID = it[position].id

            val completable = if (mode == HistoryActivity.MODE_FAVORITE) {
                repository.deleteFavorite(it[position].id)
            } else {
                repository.deleteHistory(it[position].id)
            }

            addDisposable(completable.subscribe { loadDataSet() })
        }
    }

    fun undoDelete() {
        lastDeleteID?.let {
            val completable = if (mode == HistoryActivity.MODE_FAVORITE) {
                repository.addFavorite(it)
            } else {
                repository.addHistory(it)
            }

            addDisposable(completable.subscribe { loadDataSet() })
        }
    }
}