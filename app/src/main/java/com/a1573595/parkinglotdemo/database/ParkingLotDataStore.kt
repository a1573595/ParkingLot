package com.a1573595.parkinglotdemo.database

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder
import androidx.datastore.rxjava3.RxDataStore

private const val DS_NAME = "settings"

val UPDATE_TIME = longPreferencesKey("update_time")

class ParkingLotDataStore {
    companion object {
        lateinit var instance: RxDataStore<Preferences>

        fun build(context: Context) {
            instance = RxPreferenceDataStoreBuilder(context, DS_NAME).build()
        }
    }
}