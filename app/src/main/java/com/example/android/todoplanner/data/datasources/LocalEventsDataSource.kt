package com.example.android.todoplanner.data.datasources

import com.example.android.todoplanner.data.model.DataEvent
import com.example.android.todoplanner.domain.EventStatus

interface LocalEventsDataSource {
    suspend fun getAllEvents(): List<DataEvent>
    suspend fun getEventById(uid: Long): DataEvent?
    suspend fun insertNewEvent(dataEvent: DataEvent)
    suspend fun deleteEventById(uid: Long)
    suspend fun updateEventStatus(uid: Long, status: EventStatus)
    suspend fun updateEvent(dataEvent: DataEvent)
    suspend fun deleteAllVisitedEvents()
    suspend fun deleteAllMissedEvents()
    suspend fun updateAllEventsStatusBeforeDate(dateBefore: String)
}
