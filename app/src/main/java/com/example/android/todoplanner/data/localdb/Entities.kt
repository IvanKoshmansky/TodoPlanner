package com.example.android.todoplanner.data.localdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.android.todoplanner.data.model.DataEvent
import com.example.android.todoplanner.domain.EventStatus

@Entity(tableName = "events_table")
data class DBEvent constructor (
    @PrimaryKey(autoGenerate = true)
    val uid: Long,
    val name: String,
    val desc: String,
    @ColumnInfo(name = "date_time")
    val dateTime: String,  // в формате гггг-мм-дд чч:мм
    val place: String,
    val status: EventStatus
) {
    fun asDataModel() = DataEvent(
        uid = this.uid,
        name = this.name,
        desc = this.desc,
        dateTime = this.dateTime,
        place = this.place,
        status = this.status
    )
}

fun List<DBEvent>.asDataModel() = map {
    it.asDataModel()
}
