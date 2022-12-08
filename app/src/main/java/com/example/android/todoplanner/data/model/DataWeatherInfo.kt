package com.example.android.todoplanner.data.model

import com.example.android.todoplanner.ErrorCode

sealed class DataWeatherInfo {
    data class WeatherInfo (val temp: Float, val description: String, val iconUrl: String?) : DataWeatherInfo()
    data class Error (val code: ErrorCode) : DataWeatherInfo()
}
