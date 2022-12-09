package com.example.android.todoplanner.data.weatherapi

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val WEATHER_API_KEY = "c7dac47ac4dfa65b8f351eb98ac40ef7"
const val WEATHER_API_UNITS = "metric"
const val WEATHER_API_DESCRIPTION_LANG = "ru"
const val WEATHER_API_ICONS_STORAGE_FMT = "https://openweathermap.org/img/wn/%s@2x.png"

private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
private const val FORECAST_QUERY = "forecast"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface WeatherApiService {
    @GET(FORECAST_QUERY)
    suspend fun getWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String,
        @Query("units") units: String,
        @Query("lang") lang: String
    ): OpenWeatherApiResponse
}

fun createWeatherApiService(): WeatherApiService {
    return retrofit.create(WeatherApiService::class.java)
}
