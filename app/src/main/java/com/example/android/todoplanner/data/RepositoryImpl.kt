package com.example.android.todoplanner.data

import com.example.android.todoplanner.ErrorCode
import com.example.android.todoplanner.data.datasources.GeoCoderDataSource
import com.example.android.todoplanner.data.datasources.LocalEventsDataSource
import com.example.android.todoplanner.data.datasources.WeatherDataSource
import com.example.android.todoplanner.data.model.DataEvent
import com.example.android.todoplanner.data.model.DataLatLon
import com.example.android.todoplanner.data.model.DataWeatherInfo
import com.example.android.todoplanner.domain.EventStatus
import com.example.android.todoplanner.domain.FromRepo
import com.example.android.todoplanner.domain.Repository
import com.example.android.todoplanner.domain.model.EventInfo
import com.example.android.todoplanner.domain.model.WeatherInfo
import com.example.android.todoplanner.getLocalDateTimeToString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RepositoryImpl @Inject constructor (
    private val geoCoderDataSource: GeoCoderDataSource,
    private val weatherDataSource: WeatherDataSource,
    private val localEventsDataSource: LocalEventsDataSource
) : Repository {

    override suspend fun getWeather(location: String, dateTime: String): FromRepo<WeatherInfo> {
        return withContext(Dispatchers.IO) {
            var result: FromRepo<WeatherInfo>? = null
            when (val latLon = geoCoderDataSource.getLatLon(location)) {
                is DataLatLon.LatLon -> {
                    result = when (val weatherData = weatherDataSource.getWeather(latLon, dateTime)) {
                        is DataWeatherInfo.WeatherInfo -> {
                            FromRepo.DataOk(
                                WeatherInfo(
                                    weatherData.temp,
                                    weatherData.description,
                                    weatherData.iconUrl
                                )
                            )
                        }
                        is DataWeatherInfo.Error -> FromRepo.DataError(weatherData.code)
                    }
                }
                is DataLatLon.Error -> {
                    result = FromRepo.DataError(latLon.code)
                }
            }
            result
        }
    }

    private val cachedEvents = mutableListOf<EventInfo>()

    private fun getAllEventsFromCache(): List<EventInfo> {
        return cachedEvents
    }

    private fun getEventByIdFromCache(uid: Long): EventInfo? {
        return cachedEvents.find { eventInfo -> eventInfo.uid == uid }
    }

    private fun deleteEventByIdInCache(uid: Long) {
        val element = cachedEvents.find { eventInfo -> eventInfo.uid == uid }
        cachedEvents.remove(element)
    }

    private fun updateEventStatusInCache(uid: Long, status: EventStatus) {
        cachedEvents.find { eventInfo -> eventInfo.uid == uid }?.status = status
    }

    private fun updateEventInCache(event: EventInfo) {
        cachedEvents.find { eventInfo -> eventInfo.uid == event.uid }?.apply {
            name = event.name
            desc = event.desc
            dateTime = event.dateTime
            place = event.place
            status = event.status
        }
    }

    private fun deleteAllVisitedEventsInCache() {
        cachedEvents.removeAll { eventInfo -> eventInfo.status == EventStatus.VISITED }
    }

    private fun deleteAllMissedEventsInCache() {
        cachedEvents.removeAll { eventInfo -> eventInfo.status == EventStatus.MISSED }
    }

    private fun updateAllEventsStatusBeforeDateInCache(dateTime: String) {
        cachedEvents
            .filter { (it.dateTime < dateTime) && (it.status != EventStatus.VISITED) }
            .forEach { it.status = EventStatus.MISSED }
    }

    override suspend fun getAllEvents(): FromRepo<List<EventInfo>> {
        val fromCache = getAllEventsFromCache()
        if (fromCache.isNotEmpty()) {
            return FromRepo.DataOk(fromCache)
        } else {
            return withContext(Dispatchers.IO) {
                val eventsData = localEventsDataSource.getAllEvents()
                if (eventsData.isNotEmpty()) {
                    val resultData = eventsData.map {
                        EventInfo(
                            uid = it.uid,
                            name = it.name,
                            desc = it.desc,
                            dateTime = it.dateTime,
                            place = it.place,
                            status = it.status
                        )
                    }
                    cachedEvents.addAll(resultData)
                    FromRepo.DataOk(resultData)
                } else {
                    FromRepo.DataError(ErrorCode.NOT_FOUND)
                }
            }
        }
    }

    override suspend fun getEventById(uid: Long): FromRepo<EventInfo> {
        val fromCache = getEventByIdFromCache(uid)
        if (fromCache != null) {
            return FromRepo.DataOk(fromCache)
        } else {
            return withContext(Dispatchers.IO) {
                val data = localEventsDataSource.getEventById(uid)
                if (data != null) {
                    FromRepo.DataOk(
                        EventInfo(
                            uid = data.uid,
                            name = data.name,
                            desc = data.desc,
                            dateTime = data.dateTime,
                            place = data.place,
                            status = data.status
                        )
                    )
                } else {
                    FromRepo.DataError(ErrorCode.NOT_FOUND)
                }
            }
        }
    }

    override suspend fun insertNewEvent(eventInfo: EventInfo) {
        withContext(Dispatchers.IO) {
            localEventsDataSource.insertNewEvent(
                DataEvent(
                    uid = eventInfo.uid,
                    name = eventInfo.name,
                    desc = eventInfo.desc,
                    dateTime = eventInfo.dateTime,
                    place = eventInfo.place,
                    status = eventInfo.status
                )
            )
            cachedEvents.clear()
        }
    }

    override suspend fun deleteEventById(uid: Long) {
        withContext(Dispatchers.IO) {
            localEventsDataSource.deleteEventById(uid)
            deleteEventByIdInCache(uid)
        }
    }

    override suspend fun updateEventStatus(uid: Long, status: EventStatus) {
        withContext(Dispatchers.IO) {
            localEventsDataSource.updateEventStatus(uid, status)
            updateEventStatusInCache(uid, status)
        }
    }

    override suspend fun updateEvent(event: EventInfo) {
        withContext(Dispatchers.IO) {
            localEventsDataSource.updateEvent(
                DataEvent(
                    uid = event.uid,
                    name = event.name,
                    desc = event.desc,
                    dateTime = event.dateTime,
                    place = event.place,
                    status = event.status
                )
            )
            updateEventInCache(event)
        }
    }

    override suspend fun deleteAllVisitedEvents() {
        withContext(Dispatchers.IO) {
            localEventsDataSource.deleteAllVisitedEvents()
            deleteAllVisitedEventsInCache()
        }
    }

    override suspend fun deleteAllMissedEvents() {
        withContext(Dispatchers.IO) {
            localEventsDataSource.deleteAllMissedEvents()
            deleteAllMissedEventsInCache()
        }
    }

    override suspend fun updateAllEventsStatus() {
        val datetime = getLocalDateTimeToString()
        withContext(Dispatchers.IO) {
            localEventsDataSource.updateAllEventsStatusBeforeDate(datetime)
            updateAllEventsStatusBeforeDateInCache(datetime)
        }
    }

}
