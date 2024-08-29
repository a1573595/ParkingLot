package com.a1573595.parkingdemo.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.a1573595.parkingdemo.common.isNullOrEmpty
import com.a1573595.parkingdemo.common.jsonConvert
import com.a1573595.parkingdemo.data.local.Favorite
import com.a1573595.parkingdemo.data.local.FavoriteDao
import com.a1573595.parkingdemo.data.local.History
import com.a1573595.parkingdemo.data.local.HistoryDao
import com.a1573595.parkingdemo.data.local.ParkingLotDao
import com.a1573595.parkingdemo.data.model.ParkingLotDataSet
import com.a1573595.parkingdemo.data.network.ParkingLotApi
import com.a1573595.parkingdemo.domain.model.ParkingLot
import com.a1573595.parkingdemo.domain.repository.ParkingLotRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import javax.inject.Inject

class ParkingLotRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val parkingLotDao: ParkingLotDao,
    private val favoriteDao: FavoriteDao,
    private val historyDao: HistoryDao,
    private val parkingLotApi: ParkingLotApi,
) : ParkingLotRepository {
    private companion object {
        val KEY_LAST_UPDATE_TIME = longPreferencesKey(name = "lastUpdateTime")
    }

    override val lastUpdateTimeFlow: Flow<Long?>
        get() = dataStore.data.map {
            it[KEY_LAST_UPDATE_TIME]
        }

    override val parkingLotListFlow: Flow<List<ParkingLot>>
        get() = parkingLotDao.getParkingLotListFlow()

    override val favoriteParkingLotListFlow: Flow<List<ParkingLot>>
        get() = favoriteDao.getParkingLotListFlow()

    override val historyParkingLotListFlow: Flow<List<ParkingLot>>
        get() = historyDao.getParkingLotListFlow()

    override suspend fun fetchParkingLotDataSet(): Unit {
        val respond = parkingLotApi.getParkingLotDataSet()
        val inputStream = GZIPInputStream(respond.byteStream())

        val buffer = ByteArray(256)
        val outputStream = ByteArrayOutputStream()

        var length: Int
        while (inputStream.read(buffer).also { length = it } >= 0) {
            outputStream.write(buffer, 0, length)
        }

        val dataSet = jsonConvert
            .decodeFromString<ParkingLotDataSet>(outputStream.toString("UTF-8"))

        val parkingLotList =
            dataSet.data.park.filterNot { it.id.isNullOrEmpty() && it.tw97x.isNullOrEmpty() && it.tw97y.isNullOrEmpty() }
                .map { ParkingLot.fromPark(it) }

        parkingLotDao.upsertAll(parkingLotList)
        dataStore.edit {
            it[KEY_LAST_UPDATE_TIME] = System.currentTimeMillis()
        }
    }

    override fun searchParkingLotPagingDataFlow(
        keyword: String,
        hasBus: Boolean,
        hasCar: Boolean,
        hasMotor: Boolean,
        hasBike: Boolean,
    ): Flow<PagingData<ParkingLot>> {
        val builder = StringBuilder()
        builder.append("SELECT * FROM ParkingLot")
        builder.append(" WHERE (name LIKE '%%$keyword%%' OR address LIKE '%%$keyword%%')")

        if (hasBus) {
            builder.append(" AND totalBus > 0")
        }
        if (hasCar) {
            builder.append(" AND totalCar > 0")
        }
        if (hasMotor) {
            builder.append(" AND totalMotor > 0")
        }
        if (hasBike) {
            builder.append(" AND totalBike > 0")
        }

        return Pager(
            config = PagingConfig(pageSize = 30, prefetchDistance = 2),
            pagingSourceFactory = { parkingLotDao.pagingSource(SimpleSQLiteQuery(builder.toString())) },
        ).flow
    }

    override suspend fun getParkingLotById(id: String): ParkingLot? = parkingLotDao.getParkingLotById(id)?.apply {
        historyDao.deleteAndUpsert(History(id))
    }

    override fun getFavoriteByIdFlow(id: String): Flow<Favorite?> = favoriteDao.getFavoriteByIdFlow(id)

    override suspend fun upsertFavoriteById(id: String): Unit = favoriteDao.upsert(Favorite(id))

    override suspend fun deleteFavoriteById(id: String): Unit = favoriteDao.delete(Favorite(id))

    override suspend fun deleteHistoryById(id: String): Unit = historyDao.delete(History(id))
}