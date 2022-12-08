package com.example.android.todoplanner.data.datasources

import com.example.android.todoplanner.data.localdb.DBEvent
import com.example.android.todoplanner.data.model.DataEvent
import com.example.android.todoplanner.data.localdb.LocalDatabase
import com.example.android.todoplanner.data.localdb.asDataModel
import com.example.android.todoplanner.domain.EventStatus
import javax.inject.Inject

class LocalEventsDataSourceImpl @Inject constructor (private val localDatabase: LocalDatabase): LocalEventsDataSource {

    override suspend fun getAllEvents(): List<DataEvent> {
        return localDatabase.databaseDao.getAllEvents().asDataModel()
    }

    override suspend fun getEventById(uid: Long): DataEvent? {
        return localDatabase.databaseDao.getEventById(uid)?.asDataModel()
    }

    override suspend fun insertNewEvent(dataEvent: DataEvent) {
        localDatabase.databaseDao.insertNewEvent(
            DBEvent(
                uid = 0,
                name = dataEvent.name,
                desc = dataEvent.desc,
                dateTime = dataEvent.dateTime,
                place = dataEvent.place,
                status = dataEvent.status
            )
        )
    }

    override suspend fun deleteEventById(uid: Long) {
        localDatabase.databaseDao.deleteEventById(uid)
    }

    override suspend fun updateEventStatus(uid: Long, status: EventStatus) {
        localDatabase.databaseDao.updateEventStatus(uid, status)
    }

    override suspend fun updateEvent(dataEvent: DataEvent) {
        localDatabase.databaseDao.updateEvent(
            dataEvent.uid,
            dataEvent.name,
            dataEvent.desc,
            dataEvent.dateTime,
            dataEvent.place,
            dataEvent.status
        )
    }

    override suspend fun deleteAllVisitedEvents() {
        localDatabase.databaseDao.deleteAllEventsByStatus(EventStatus.VISITED)
    }

    override suspend fun deleteAllMissedEvents() {
        localDatabase.databaseDao.deleteAllEventsByStatus(EventStatus.MISSED)
    }

    override suspend fun updateAllEventsStatusBeforeDate(dateBefore: String) {
        localDatabase.databaseDao.updateAllEventsStatusBeforeDate(dateBefore, EventStatus.VISITED, EventStatus.MISSED)
    }

}
