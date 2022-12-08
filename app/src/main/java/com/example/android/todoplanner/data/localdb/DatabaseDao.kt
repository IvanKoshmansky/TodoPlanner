package com.example.android.todoplanner.data.localdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.todoplanner.domain.EventStatus

@Dao
interface DatabaseDao {

    // запросить все мероприятия из хранилища с сортировкой по дате
    @Query("select * from events_table order by date_time")
    fun getAllEvents(): List<DBEvent>

    // запросить мероприятие с данным id
    @Query("select * from events_table where uid = :uid")
    fun getEventById(uid: Long): DBEvent?

    // добавить новое мероприятие
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewEvent(event: DBEvent)

    // удалить мероприятие по uid
    @Query("delete from events_table where uid = :uid")
    fun deleteEventById(uid: Long)

    // обновить статус события
    @Query("update events_table set status = :status where uid = :uid")
    fun updateEventStatus(uid: Long, status: EventStatus)

    // обновить событие полностью
    @Query("update events_table set name = :name, desc = :desc, date_time = :dt, place = :place, status = :status where uid = :uid")
    fun updateEvent(uid: Long, name: String, desc: String, dt: String, place: String, status: EventStatus)

    // удалить все события со статусом status
    @Query("delete from events_table where status = :status")
    fun deleteAllEventsByStatus(status: EventStatus)

    // обновить статусы событий
    @Query("update events_table set status = :newStatus where date_time < :dt and status != :skipStatus")
    fun updateAllEventsStatusBeforeDate(dt: String, skipStatus: EventStatus, newStatus: EventStatus)

}
