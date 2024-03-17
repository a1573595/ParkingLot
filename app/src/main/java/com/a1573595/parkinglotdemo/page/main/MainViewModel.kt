package com.a1573595.parkinglotdemo.page.main

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.a1573595.parkinglotdemo.BaseViewModel
import com.a1573595.parkinglotdemo.database.ParkingLot
import com.a1573595.parkinglotdemo.repository.ParkingLotRepository
import com.a1573595.parkinglotdemo.tool.Event
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : BaseViewModel {
    constructor(application: Application) : super(application) {
        repository = ParkingLotRepository()
    }

    constructor(application: Application, repository: ParkingLotRepository) : super(application) {
        this.repository = repository
    }

    private val repository: ParkingLotRepository
    val updateTimeEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val dataSetEvent: MutableLiveData<Event<List<ParkingLot>>> = MutableLiveData()

    fun loadDataSet() {
        addDisposable(
            repository.getUpdateTime()
                .subscribe({
                    getParkingLots(it)
                }, {
                    updateDataSet()
                })
        )
    }

    fun updateDataSet() {
        addDisposable(repository.downloadDataSet()
            .subscribe { _ ->
                loadDataSet()
            }
        )
    }

    private fun getParkingLots(updateTime: Long) {
        addDisposable(repository.getParkingLots()
            .subscribe { list ->
                dataSetEvent.postValue(Event(list))
                updateTimeEvent.postValue(Event(calTimeMilliToTime(updateTime)))
            }
        )
    }

    private fun calTimeMilliToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        return format.format(date)
    }
}