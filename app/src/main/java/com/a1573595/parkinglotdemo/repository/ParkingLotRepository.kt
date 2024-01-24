package com.a1573595.parkinglotdemo.repository

import androidx.datastore.preferences.core.MutablePreferences
import androidx.sqlite.db.SimpleSQLiteQuery
import com.a1573595.parkinglotdemo.api.NetWorkService
import com.a1573595.parkinglotdemo.database.*
import com.a1573595.parkinglotdemo.model.TaipeiParkingLotInfo
import com.a1573595.parkinglotdemo.tool.LatLngCoding
import com.google.gson.Gson
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream

class ParkingLotRepository {
    fun getUpdateTime(): Flowable<Long> =
        ParkingLotDataStore.ds.data().map { it[UPDATE_TIME]!! }
            .subscribeOn(Schedulers.io())

    fun downloadDataSet(): Single<List<Long>> =
        NetWorkService.apiInterface.downloadFileWithDynamicUrlSync("TCMSV_alldesc.gz")
            .map { body ->
                val inputStream = body.byteStream()
                val unGzip = GZIPInputStream(inputStream)

                val buffer = ByteArray(256)
                val out = ByteArrayOutputStream()

                var length: Int
                while (unGzip.read(buffer).also { length = it } >= 0) {
                    out.write(buffer, 0, length)
                }
                val info: TaipeiParkingLotInfo =
                    Gson().fromJson(out.toString("UTF-8"), TaipeiParkingLotInfo::class.java)

                val list: MutableList<ParkingLot> = mutableListOf()
                var latLng: List<String>

                info.data.park.filterNot { it.id.isNullOrEmpty() }.forEach {
                    latLng = LatLngCoding.calTWD97ToLonLat(it.tw97x, it.tw97y).split(",")
                    list.add(
                        ParkingLot(
                            it.id!!, it.area, it.name, it.summary, it.address,
                            it.tel, it.payex, it.totalcar,
                            it.totalmotor, it.totalbike, it.totalbus,
                            latLng[0].toDouble(), latLng[1].toDouble()
                        )
                    )
                }
                list
            }
            .flatMap { deleteDataSet().toSingle { it } }
            .flatMap { writeDataSet(it) }
            .doOnSuccess {
                ParkingLotDataStore.ds.updateDataAsync { prefsIn ->
                    val mutablePreferences: MutablePreferences =
                        prefsIn.toMutablePreferences()
                    mutablePreferences[UPDATE_TIME] = System.currentTimeMillis()
                    Single.just(mutablePreferences)
                }.subscribe()
            }
            .subscribeOn(Schedulers.io())

    fun deleteDataSet(): Completable =
        ParkingLotDatabase.instance.getParkingDao()
            .deleteAll()
            .subscribeOn(Schedulers.io())

    fun writeDataSet(list: List<ParkingLot>): Single<List<Long>> =
        ParkingLotDatabase.instance.getParkingDao()
            .insertAll(list)
            .subscribeOn(Schedulers.io())

    fun getParkingLots(): Single<List<ParkingLot>> =
        ParkingLotDatabase.instance.getParkingDao()
            .getAll()
            .subscribeOn(Schedulers.io())

    fun getParkingLot(id: String): Single<ParkingLot> =
        ParkingLotDatabase.instance.getParkingDao()
            .getByID(id)
            .flatMap { addHistory(it.id).toSingle { it } }
            .subscribeOn(Schedulers.io())

    fun searchParkingLots(query: String): Single<List<ParkingLot>> =
        ParkingLotDatabase.instance.getParkingDao()
            .getAllByQuery(SimpleSQLiteQuery(query))
            .subscribeOn(Schedulers.io())

    fun getHistory(): Single<List<ParkingLot>> =
        ParkingLotDatabase.instance.getHistoryDao()
            .getHistoryList()
            .subscribeOn(Schedulers.io())

    fun addHistory(id: String): Completable =
        ParkingLotDatabase.instance.getHistoryDao()
            .insert(History(id))
            .subscribeOn(Schedulers.io())

    fun deleteHistory(id: String): Completable =
        ParkingLotDatabase.instance.getHistoryDao()
            .deleteByID(id)
            .subscribeOn(Schedulers.io())

    fun getFavorites(): Single<List<ParkingLot>> =
        ParkingLotDatabase.instance.getFavoriteDao()
            .getLoveList()
            .subscribeOn(Schedulers.io())

    fun getFavorites(id: String): Single<Favorite> =
        ParkingLotDatabase.instance.getFavoriteDao()
            .getByID(id)
            .subscribeOn(Schedulers.io())

    fun addFavorite(id: String): Completable =
        ParkingLotDatabase.instance.getFavoriteDao()
            .insert(Favorite(id))
            .subscribeOn(Schedulers.io())

    fun deleteFavorite(id: String): Completable =
        ParkingLotDatabase.instance.getFavoriteDao()
            .deleteByID(id)
            .subscribeOn(Schedulers.io())
}