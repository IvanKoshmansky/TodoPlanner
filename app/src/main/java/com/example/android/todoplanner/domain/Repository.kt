package com.example.android.todoplanner.domain

import com.example.android.todoplanner.domain.model.EventInfo
import com.example.android.todoplanner.domain.model.WeatherInfo

interface Repository {
    suspend fun getWeather(location: String, dateTime: String): FromRepo<WeatherInfo>
    suspend fun getAllEvents(): FromRepo<List<EventInfo>>
    suspend fun getEventById(uid: Long): FromRepo<EventInfo>
    suspend fun insertNewEvent(eventInfo: EventInfo)
    suspend fun deleteEventById(uid: Long)
    suspend fun updateEventStatus(uid: Long, status: EventStatus)
    suspend fun updateEvent(event: EventInfo)
    suspend fun deleteAllVisitedEvents()
    suspend fun deleteAllMissedEvents()
    suspend fun updateAllEventsStatus()
}
