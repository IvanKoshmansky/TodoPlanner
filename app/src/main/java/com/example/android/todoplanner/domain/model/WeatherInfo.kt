package com.example.android.todoplanner.domain.model

data class WeatherInfo (
    val temp: Float,
    val description: String,
    val iconUrl: String?
) {
    companion object {
        fun fillEmpty() = WeatherInfo(
            temp = 0.0f,
            description = "",
            iconUrl = null
        )
    }
}
