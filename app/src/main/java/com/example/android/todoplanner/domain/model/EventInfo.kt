package com.example.android.todoplanner.domain.model

import com.example.android.todoplanner.domain.EventStatus

data class EventInfo (
    var uid: Long,
    var name: String,
    var desc: String,
    var dateTime: String,  // в формате гггг-мм-дд чч:мм
    var place: String,
    var status: EventStatus
) {
    companion object {
        fun fillEmpty() = EventInfo(
            uid = 0,
            name = "",
            desc = "",
            dateTime = "",
            place = "",
            status = EventStatus.UNKNOWN
        )
    }
}
