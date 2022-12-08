package com.example.android.todoplanner.presentation

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.android.todoplanner.R
import com.example.android.todoplanner.domain.EventStatus
import java.util.*

private val dateTimeRegex = Regex("""(\d\d\d\d)-(\d\d)-(\d\d) (\d\d):(\d\d)""")

// строковая дата-время в формат с днем недели
@BindingAdapter("dateTimeToHuman")
fun TextView.dateTimeToHuman(dateTime: String) {
    var result = ""
    val match = dateTimeRegex.find(dateTime)
    match?.let {
        val values = it.groupValues.drop(1)
        val year = values[0].toInt()
        val month = values[1].toInt() - 1
        val day = values[2].toInt()
        val hour = values[3].toInt()
        val minute = values[4].toInt()
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        result = when (dayOfWeek) {
            Calendar.MONDAY    -> "Понедельник, "
            Calendar.TUESDAY   -> "Вторник, "
            Calendar.WEDNESDAY -> "Среда, "
            Calendar.THURSDAY  -> "Четверг, "
            Calendar.FRIDAY    -> "Пятница, "
            Calendar.SATURDAY  -> "Суббота, "
            Calendar.SUNDAY    -> "Воскресенье, "
            else -> ""
        }
        result += "$day "
        when (month) {
            0 -> result += "января, "
            1 -> result += "февраля, "
            2 -> result += "марта, "
            3 -> result += "апреля, "
            4 -> result += "мая, "
            5 -> result += "июня, "
            6 -> result += "июля, "
            7 -> result += "августа, "
            8 -> result += "сентября, "
            9 -> result += "октября, "
            10 -> result += "ноября, "
            11 -> result += "декабря, "
            else -> {}
        }
        result += "%d:%02d".format(hour, minute)
    }
    text = result
}

// иконка статуса мероприятия по статусу мероприятия
@BindingAdapter("eventStatusToDrawable")
fun ImageView.eventStatusToDrawable(eventStatus: EventStatus) {
    when (eventStatus) {
        EventStatus.UNKNOWN -> setImageResource(R.drawable.ic_sync_48)
        EventStatus.ACTIVE -> setImageResource(R.drawable.ic_event_upcoming_48)
        EventStatus.VISITED -> setImageResource(R.drawable.ic_done_outline_48)
        EventStatus.MISSED -> setImageResource(R.drawable.ic_history_48)
        else -> {}
    }
}
