package com.a1573595.parkingdemo.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.a1573595.parkingdemo.data.local.FavoriteDao
import com.a1573595.parkingdemo.data.local.HistoryDao
import com.a1573595.parkingdemo.data.local.ParkingLotDao
import com.a1573595.parkingdemo.data.local.ParkingLotDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {
    @Provides
    @Singleton
    fun provideParkingLotDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = {
                appContext.preferencesDataStoreFile("parkingLot_preferences")
            }
        )

    @Provides
    @Singleton
    fun provideParkingLotDatabase(
        application: Application
    ): ParkingLotDatabase {
        return Room.databaseBuilder(
            context = application,
            klass = ParkingLotDatabase::class.java,
            name = "news_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideParkingLotDao(
        database: ParkingLotDatabase
    ): ParkingLotDao = database.parkingLotDao

    @Provides
    @Singleton
    fun provideFavoriteDao(
        database: ParkingLotDatabase
    ): FavoriteDao = database.favoriteDao

    @Provides
    @Singleton
    fun provideHistoryDao(
        database: ParkingLotDatabase
    ): HistoryDao = database.historyDao
}