package com.example.android.todoplanner.data.datasources

import com.example.android.todoplanner.data.model.DataLatLon
import com.example.android.todoplanner.data.model.DataWeatherInfo

interface WeatherDataSource {
    suspend fun getWeather(latLon: DataLatLon.LatLon, dateTime: String): DataWeatherInfo
}
