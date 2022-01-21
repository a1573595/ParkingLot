package com.a1573595.parkinglotdemo

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.a1573595.parkinglotdemo.database.ParkingLot
import com.a1573595.parkinglotdemo.database.ParkingLotDataStore
import com.a1573595.parkinglotdemo.page.history.HistoryActivity.Companion.MODE_FAVORITE
import com.a1573595.parkinglotdemo.page.history.HistoryActivity.Companion.MODE_HISTORY
import com.a1573595.parkinglotdemo.page.history.HistoryModel
import com.a1573595.parkinglotdemo.repository.ParkingLotRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HistoryModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val rxSchedulerRule = RxSchedulerRule()

    private val context: Application = ApplicationProvider.getApplicationContext()

    private lateinit var viewModel: HistoryModel
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
//        ParkingLotDatabase.build(context)

        viewModel = HistoryModel(context, repository)

        every { repository.getFavorites() }.returns(Single.just(parkingLots))
        every { repository.getHistory() }.returns(Single.just(parkingLots))
    }

    @Test
    fun load_favorite_dataset() {
        viewModel.setMode(MODE_FAVORITE)
        viewModel.loadDataSet()

        Assert.assertEquals(
            parkingLots,
            viewModel.dataSetEvent.value?.peekContent()
        )
    }

    @Test
    fun load_history_dataset() {
        viewModel.setMode(MODE_HISTORY)
        viewModel.loadDataSet()

        Assert.assertEquals(
            parkingLots,
            viewModel.dataSetEvent.value?.peekContent()
        )
    }

    @Test
    fun delete_favorite() {
        viewModel.setMode(MODE_FAVORITE)
        viewModel.loadDataSet()

        every { repository.deleteFavorite(any()) }.returns(Completable.complete())

        viewModel.delete(1)

        Assert.assertEquals(
            parkingLots,
            viewModel.dataSetEvent.value?.peekContent()
        )
    }

    @Test
    fun delete_history() {
        viewModel.setMode(MODE_HISTORY)
        viewModel.loadDataSet()

        every { repository.deleteHistory(any()) }.returns(Completable.complete())

        viewModel.delete(1)

        Assert.assertEquals(
            parkingLots,
            viewModel.dataSetEvent.value?.peekContent()
        )
    }

    @Test
    fun undo_delete_favorite() {
        viewModel.setMode(MODE_FAVORITE)
        viewModel.loadDataSet()

        every { repository.deleteFavorite(any()) }.returns(Completable.complete())

        viewModel.delete(0)
        viewModel.undoDelete()

        Assert.assertEquals(
            parkingLots,
            viewModel.dataSetEvent.value?.peekContent()
        )
    }

    @Test
    fun undo_delete_history() {
        viewModel.setMode(MODE_HISTORY)
        viewModel.loadDataSet()

        every { repository.deleteHistory(any()) }.returns(Completable.complete())

        viewModel.delete(0)
        viewModel.undoDelete()

        Assert.assertEquals(
            parkingLots,
            viewModel.dataSetEvent.value?.peekContent()
        )
    }
}