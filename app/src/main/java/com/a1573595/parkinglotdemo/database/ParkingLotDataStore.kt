package com.a1573595.parkinglotdemo.database

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder
import androidx.datastore.rxjava3.RxDataStore

private const val DS_NAME = "settings"

val UPDATE_TIME = longPreferencesKey("update_time")

object ParkingLotDataStore {
    private lateinit var _ds: RxDataStore<Preferences>

    val ds: RxDataStore<Preferences>
        get() = _ds

    fun build(context: Context) {
        _ds = RxPreferenceDataStoreBuilder(context, DS_NAME).build()
    }
}