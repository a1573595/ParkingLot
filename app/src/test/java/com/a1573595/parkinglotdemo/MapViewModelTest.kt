package com.a1573595.parkinglotdemo

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.a1573595.parkinglotdemo.database.ParkingLot
import com.a1573595.parkinglotdemo.database.ParkingLotDataStore
import com.a1573595.parkinglotdemo.page.map.MapViewModel
import com.a1573595.parkinglotdemo.repository.ParkingLotRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val rxSchedulerRule = RxSchedulerRule()

    private val context: Application = ApplicationProvider.getApplicationContext()

    private lateinit var viewModel: MapViewModel
    private val repository: ParkingLotRepository = mockk(relaxed = true)

    private val parkingLots = listOf(
        ParkingLot("001", "", "A", "", "", "", ""),
        ParkingLot("002", "", "B", "", "", "", ""),
        ParkingLot("003", "", "C", "", "", "", ""),
        ParkingLot("004", "", "D", "", "", "", ""),
        ParkingLot("005", "", "E", "", "", "", ""),
        ParkingLot("006", "", "F", "", "", "", ""),
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        ParkingLotDataStore.build(context)

        viewModel = MapViewModel(context, repository)
    }

    @Test
    fun loadDataSet() {
        every { repository.getParkingLots() } returns Single.just(parkingLots)

        viewModel.loadDataSet()

        Assert.assertEquals(
            parkingLots,
            viewModel.dataSetEvent.value?.peekContent()
        )
    }
}