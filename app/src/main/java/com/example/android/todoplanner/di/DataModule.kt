package com.example.android.todoplanner.di

import com.example.android.todoplanner.TodoApplication
import com.example.android.todoplanner.data.RepositoryImpl
import com.example.android.todoplanner.data.datasources.*
import com.example.android.todoplanner.data.localdb.LocalDatabase
import com.example.android.todoplanner.data.localdb.getDatabase
import com.example.android.todoplanner.data.weatherapi.WeatherApiService
import com.example.android.todoplanner.data.weatherapi.createWeatherApiService
import com.example.android.todoplanner.domain.Repository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {

    @Singleton
    @Provides
    fun provideWeatherApi(): WeatherApiService {
        return createWeatherApiService()
    }

    @Singleton
    @Provides
    fun provideWeatherDataSource(weatherApiService: WeatherApiService): WeatherDataSource {
        return WeatherDataSourceImpl(weatherApiService)
    }

    @Singleton
    @Provides
    fun provideGeoCoderDataSource(context: TodoApplication): GeoCoderDataSource {
        return GeoCoderDataSourceImpl(context)
    }

    @Singleton
    @Provides
    fun provideLocalDatabase(context: TodoApplication): LocalDatabase {
        return getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideLocalEventsDataSource(localDatabase: LocalDatabase): LocalEventsDataSource {
        return LocalEventsDataSourceImpl(localDatabase)
    }

    @Singleton
    @Provides
    fun provideRepository(geoCoderDataSource: GeoCoderDataSource,
                          weatherDataSource: WeatherDataSource,
                          localEventsDataSource: LocalEventsDataSource): Repository {
        return RepositoryImpl(geoCoderDataSource, weatherDataSource, localEventsDataSource)
    }

}
