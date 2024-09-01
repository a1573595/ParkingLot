package com.a1573595.parkingdemo.di

import android.content.Context
import com.a1573595.parkingdemo.BuildConfig
import com.a1573595.parkingdemo.common.Constants
import com.a1573595.parkingdemo.data.network.ParkingLotApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        interceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    @Provides
    @Singleton
    fun providesParkingLotApi(
        client: OkHttpClient,
    ): ParkingLotApi = Retrofit.Builder()
        .client(client)
        .baseUrl(Constants.BASE_URL)
        .build()
        .create(ParkingLotApi::class.java)
}