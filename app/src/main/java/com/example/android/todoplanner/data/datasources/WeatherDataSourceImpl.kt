package com.example.android.todoplanner.data.datasources

import com.example.android.todoplanner.ErrorCode
import com.example.android.todoplanner.data.model.DataLatLon
import com.example.android.todoplanner.data.model.DataWeatherInfo
import com.example.android.todoplanner.data.weatherapi.*
import com.example.android.todoplanner.localDateTimeToUnix
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import kotlin.math.abs

private const val FORECAST_PERIOD_HOURS = 3

class WeatherDataSourceImpl @Inject constructor (
    private val weatherApiService: WeatherApiService
) : WeatherDataSource {

    private fun parseApiResponse(response: OpenWeatherApiResponse, dateTime: String): DataWeatherInfo {
        var result: DataWeatherInfo.WeatherInfo? = null
        if (response.list.isNotEmpty()) {
            val searchDateTimeUnix = localDateTimeToUnix(dateTime)
            searchDateTimeUnix?.let { unixTime ->
                val match = response.list.find {
                    abs(it.unixTime - unixTime) <= FORECAST_PERIOD_HOURS * 3600
                }
                match?.let { item ->
                    result = DataWeatherInfo.WeatherInfo(
                        item.main.temp.toFloat(),
                        if (item.weather.isNotEmpty()) { item.weather[0].description } else { "" },
                        if (item.weather.isNotEmpty()) {
                            String.format(WEATHER_API_ICONS_STORAGE_FMT, item.weather[0].icon)
                        } else { null }
                    )
                }
            }
        }
        return result ?: DataWeatherInfo.Error(ErrorCode.NOT_FOUND)
    }

    override suspend fun getWeather(latLon: DataLatLon.LatLon, dateTime: String): DataWeatherInfo {
        var result: DataWeatherInfo? = null
        var errCode = ErrorCode.UNKNOWN_ERROR
        try {
            val response = weatherApiService.getWeather(
                "${latLon.lat}",
                "${latLon.lon}",
                WEATHER_API_KEY,
                WEATHER_API_UNITS,
                WEATHER_API_DESCRIPTION_LANG
            )
            result = parseApiResponse(response, dateTime)
        } catch (throwable: Throwable) {
            errCode = when (throwable) {
                is IOException -> {
                    ErrorCode.TIMEOUT
                }
                is HttpException -> {
                    when (throwable.code()) {
                        401, 403 -> ErrorCode.AUTH_ERROR
                        404 -> ErrorCode.NOT_FOUND
                        500 -> ErrorCode.INTERNAL_ERROR
                        503 -> ErrorCode.SERVICE_UNAVAILABLE
                        else -> ErrorCode.PROTOCOL_ERROR
                    }
                }
                else -> {
                    ErrorCode.UNKNOWN_ERROR
                }
            }
            throwable.printStackTrace()
        }
        return result ?: DataWeatherInfo.Error(errCode)
    }
}
