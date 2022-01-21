package com.a1573595.parkinglotdemo

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.a1573595.parkinglotdemo.database.Favorite
import com.a1573595.parkinglotdemo.database.ParkingLot
import com.a1573595.parkinglotdemo.database.ParkingLotDataStore
import com.a1573595.parkinglotdemo.page.detail.DetailViewModel
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
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class DetailViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val rxSchedulerRule = RxSchedulerRule()

    private val context: Application = ApplicationProvider.getApplicationContext()

    private lateinit var viewModel: DetailViewModel
    private val repository: ParkingLotRepository = mockk(relaxed = true)

    private val parkingLot = ParkingLot("001", "", "A", "", "", "", "")

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        ParkingLotDataStore.build(context)

        viewModel = DetailViewModel(context, repository)
    }

    @Test
    fun load_favorite_data() {
        val id = "001"
        every { repository.getFavorites(any()) }.returns(Single.just(Favorite(id)))
        every { repository.getParkingLot(any()) }.returns(Single.just(parkingLot))
        every { repository.addHistory(any()) }.returns(Completable.complete())

        viewModel.loadData(id)

        Assert.assertEquals(
            true,
            viewModel.isFavoriteEvent.value?.peekContent()
        )

        Assert.assertEquals(
            parkingLot,
            viewModel.parkingLotEvent.value?.peekContent()
        )
    }

    @Test
    fun load_unfavorite_data() {
        val id = "002"
        every { repository.getFavorites(any()) }.returns(Single.error(Exception()))
        every { repository.getParkingLot(any()) }.returns(Single.just(parkingLot))
        every { repository.addHistory(any()) }.returns(Completable.complete())

        viewModel.loadData(id)

        Assert.assertEquals(
            false,
            viewModel.isFavoriteEvent.value?.peekContent()
        )

        Assert.assertEquals(
            parkingLot,
            viewModel.parkingLotEvent.value?.peekContent()
        )
    }

    @Test
    fun addFavorite() {
        every { repository.deleteFavorite(any()) }.returns(Completable.complete())
        every { repository.addFavorite(any()) }.returns(Completable.complete())

        viewModel.addFavorite("001")

        Assert.assertEquals(
            true,
            viewModel.isFavoriteEvent.value?.peekContent()
        )

        viewModel.addFavorite("001")

        Assert.assertEquals(
            false,
            viewModel.isFavoriteEvent.value?.peekContent()
        )
    }
}