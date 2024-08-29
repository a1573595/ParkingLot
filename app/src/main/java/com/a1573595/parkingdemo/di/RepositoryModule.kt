package com.a1573595.parkingdemo.di

import com.a1573595.parkingdemo.data.repository.ParkingLotRepositoryImpl
import com.a1573595.parkingdemo.domain.repository.ParkingLotRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provideParkingLotRepository(newsRepositoryImpl: ParkingLotRepositoryImpl): ParkingLotRepository = newsRepositoryImpl
}