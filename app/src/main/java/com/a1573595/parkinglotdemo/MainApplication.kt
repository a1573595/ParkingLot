package com.a1573595.parkinglotdemo

import android.app.Application
import android.content.Context
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.stringPreferencesKey
import com.a1573595.parkinglotdemo.database.ParkingLotDataStore
import com.a1573595.parkinglotdemo.database.ParkingLotDatabase
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import timber.log.Timber
import java.util.*

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        ParkingLotDataStore.build(this)

        ParkingLotDataStore.instance.updateDataAsync {
            val mutablePreferences = it.toMutablePreferences()
            val password = it[stringPreferencesKey("DatabaseKey")] ?: UUID.randomUUID().toString()
            mutablePreferences[stringPreferencesKey("DatabaseKey")] = password

            ParkingLotDatabase.build(this, password)

            Single.just(mutablePreferences)
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        RxJavaPlugins.setErrorHandler {
            Timber.e(it)
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun attachBaseContext(base: Context) {
        val configuration = base.resources.configuration

        if (configuration.fontScale != 1.0f) {
            configuration.fontScale = 1.0f
            super.attachBaseContext(base.createConfigurationContext(configuration))
            return
        }

        super.attachBaseContext(base)
    }

    override fun getResources(): Resources {
        val resources = super.getResources()

        if (resources?.configuration?.fontScale != 1.0f) {
            val configuration = resources.configuration
            configuration.fontScale = 1.0f

            val context = createConfigurationContext(configuration)
            return context.resources
        }

        return resources
    }
}