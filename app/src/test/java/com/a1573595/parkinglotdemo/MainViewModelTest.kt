package com.a1573595.parkinglotdemo

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.a1573595.parkinglotdemo.database.ParkingLot
import com.a1573595.parkinglotdemo.page.main.MainViewModel
import com.a1573595.parkinglotdemo.repository.ParkingLotRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Remove android:name=".MainApplication" in AndroidManifest for test
 * android-sqlcipher-database only run on Android instead of JVM.
 * https://github.com/sqlcipher/android-database-sqlcipher/issues/105
 */
@RunWith(AndroidJUnit4::class)
class MainViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val rxSchedulerRule = RxSchedulerRule()

    private val context: Application = ApplicationProvider.getApplicationContext()

    private lateinit var viewModel: MainViewModel
    private val repository: ParkingLotRepository = mockk(relaxed = true)

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        viewModel = MainViewModel(context, repository)
    }

    @Test
    fun load_data_set() {
        every { repository.getUpdateTime() }.returns(Flowable.just(1000L))
        every { repository.getParkingLots() }.returns(Single.just(emptyList()))

        viewModel.loadDataSet()

        Assert.assertNotNull(
            viewModel.updateTimeEvent.value
        )

        Assert.assertEquals(
            emptyList<ParkingLot>(),
            viewModel.dataSetEvent.value?.peekContent()
        )
    }

    @Test
    fun updateDataSet() {
        every { repository.downloadDataSet() }.returns(Single.just(emptyList()))
        every { repository.deleteDataSet() }.returns(Completable.complete())
        every { repository.writeDataSet(any()) }.returns(Single.just(emptyList()))
        every { repository.getUpdateTime() }.returns(Flowable.just(1000L))
        every { repository.getParkingLots() }.returns(Single.just(emptyList()))

        viewModel.updateDataSet()

        Assert.assertNotNull(
            viewModel.updateTimeEvent.value
        )

        Assert.assertEquals(
            emptyList<ParkingLot>(),
            viewModel.dataSetEvent.value?.peekContent()
        )
    }
}