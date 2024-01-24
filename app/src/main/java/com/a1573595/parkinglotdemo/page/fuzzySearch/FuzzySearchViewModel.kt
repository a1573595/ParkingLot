package com.a1573595.parkinglotdemo.page.fuzzySearch

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.a1573595.parkinglotdemo.BaseViewModel
import com.a1573595.parkinglotdemo.database.ParkingLot
import com.a1573595.parkinglotdemo.repository.ParkingLotRepository
import com.a1573595.parkinglotdemo.tool.Event

class FuzzySearchViewModel : BaseViewModel {
    constructor(application: Application) : super(application) {
        repository = ParkingLotRepository()
    }

    constructor(application: Application, repository: ParkingLotRepository) : super(application) {
        this.repository = repository
    }

    private val repository: ParkingLotRepository

    private var mode = 1
    private var keyword = ""

    val dataSetEvent: MutableLiveData<Event<List<ParkingLot>>> = MutableLiveData()

    fun setKeyword(keyword: String) {
        this.keyword = keyword

        loadDataSet()
    }

    fun setMode(mode: Int) {
        this.mode = mode

        loadDataSet()
    }

    fun loadDataSet() {
        var query = "SELECT * FROM TABLE_PARKING_LOT WHERE "

        query += when (mode) {
            0 -> "totalbus > 0"
            1 -> "totalcar > 0"
            2 -> "totalmotor > 0"
            else -> "totalbike > 0"
        }

        if (keyword.isNotEmpty()) {
            query += String.format(
                " AND (name LIKE '%%%s%%' OR address LIKE '%%%s%%')",
                keyword,
                keyword
            )
        }

        addDisposable(repository.searchParkingLots(query).subscribe { list ->
            dataSetEvent.postValue(Event(list))
        })
    }
}