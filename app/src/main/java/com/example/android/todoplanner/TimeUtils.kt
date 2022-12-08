package com.example.android.todoplanner

import java.util.*

fun getLocalDateTimeToString(): String {
    val cal = Calendar.getInstance(TimeZone.getDefault())
    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH)
    val day = cal.get(Calendar.DAY_OF_MONTH)
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    val minute = cal.get(Calendar.MINUTE)
    return String.format("%04d-%02d-%02d %02d:%02d", year, month + 1, day, hour, minute)
}

fun localDateTimeToUnix(dateTime: String): Long? {
    val dateTimeRegex = Regex("""(\d\d\d\d)-(\d\d)-(\d\d) (\d\d):(\d\d)""")
    var result: Long? = null
    val match = dateTimeRegex.find(dateTime)
    match?.let {
        val values = it.groupValues.drop(1)
        val year = values[0].toInt()
        val month = values[1].toInt() - 1
        val day = values[2].toInt()
        val hour = values[3].toInt()
        val minute = values[4].toInt()
        val cal = Calendar.getInstance(TimeZone.getDefault())
        cal.set(year, month, day, hour, minute)
        result = cal.timeInMillis / 1000  // по Гринвичу
    }
    return result
}
