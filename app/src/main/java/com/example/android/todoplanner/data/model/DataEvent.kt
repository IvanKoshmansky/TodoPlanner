package com.example.android.todoplanner.data.model

import com.example.android.todoplanner.domain.EventStatus

data class DataEvent (
    val uid: Long,
    val name: String,
    val desc: String,
    val dateTime: String,
    val place: String,
    val status: EventStatus
)
