package com.example.android.todoplanner.data.weatherapi

import com.squareup.moshi.Json

// погода на пять дней с интервалом в три часа
data class ApiMain (
    val temp: Double,
    @Json(name = "feels_like")
    val feelsLike: Double,
    val pressure: Double,
    val humidity: Double
)

data class ApiWeather (
    val id: Long,
    val main: String,
    val description: String,
    val icon: String
)

data class ApiOneHourItem (
    @Json(name = "dt")
    val unixTime: Long,
    val main: ApiMain,
    val weather: List<ApiWeather>,
)

data class OpenWeatherApiResponse (
    val list: List<ApiOneHourItem>
)
