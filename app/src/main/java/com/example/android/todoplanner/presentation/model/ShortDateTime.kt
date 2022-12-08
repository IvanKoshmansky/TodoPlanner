package com.example.android.todoplanner.presentation.model

data class ShortDate (val year: Int, val month: Int, val day: Int)

data class ShortTime (val hour: Int, val minute: Int)

data class ShortDateTime (var date: ShortDate, var time: ShortTime) {
    override fun toString() = String.format("%04d-%02d-%02d %02d:%02d",
        date.year,
        date.month + 1,
        date.day,
        time.hour,
        time.minute
    )
}
