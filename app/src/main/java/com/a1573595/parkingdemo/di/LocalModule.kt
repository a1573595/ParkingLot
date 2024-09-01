package com.a1573595.parkingdemo.di

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.a1573595.parkingdemo.data.local.FavoriteDao
import com.a1573595.parkingdemo.data.local.HistoryDao
import com.a1573595.parkingdemo.data.local.ParkingLotDao
import com.a1573595.parkingdemo.data.local.ParkingLotDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import java.util.UUID
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {
    @Provides
    @Singleton
    fun provideParkingLotDataStore(
        @ApplicationContext appContext: Context,
    ): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = {
                appContext.preferencesDataStoreFile("parking_lot_preferences")
            }
        )

    @Provides
    @Singleton
    fun provideParkingLotSharedPreference(
        @ApplicationContext appContext: Context,
    ): EncryptedSharedPreferences =
        EncryptedSharedPreferences.create(
            appContext,
            "parking_lot_security",
            MasterKey.Builder(appContext).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        ) as EncryptedSharedPreferences

    @SuppressLint("ApplySharedPref")
    @Provides
    @Singleton
    fun provideParkingLotDatabase(
        application: Application,
        sharedPreferences: EncryptedSharedPreferences,
    ): ParkingLotDatabase {
        System.loadLibrary("sqlcipher")

        var key = sharedPreferences.getString("database_key", null)
        if (key.isNullOrEmpty()) {
            key = UUID.randomUUID().toString()
            sharedPreferences.edit().putString("database_key", key).commit()
        }

        return Room.databaseBuilder(
            context = application,
            klass = ParkingLotDatabase::class.java,
            name = "parking_lot_db"
        )
            .openHelperFactory(SupportOpenHelperFactory(key.toByteArray()))
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideParkingLotDao(
        database: ParkingLotDatabase,
    ): ParkingLotDao = database.parkingLotDao

    @Provides
    @Singleton
    fun provideFavoriteDao(
        database: ParkingLotDatabase,
    ): FavoriteDao = database.favoriteDao

    @Provides
    @Singleton
    fun provideHistoryDao(
        database: ParkingLotDatabase,
    ): HistoryDao = database.historyDao
}