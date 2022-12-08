package com.example.android.todoplanner.presentation.model

import com.example.android.todoplanner.domain.EventStatus

data class ViewEventShortInfo (
    val uid: Long,
    val name: String,
    val dateTime: String,
    val status: EventStatus
)
