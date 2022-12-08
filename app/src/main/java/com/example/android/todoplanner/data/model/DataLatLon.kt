package com.example.android.todoplanner.data.model

import com.example.android.todoplanner.ErrorCode

sealed class DataLatLon {
    class LatLon (val lat: Double, val lon: Double) : DataLatLon()
    class Error (val code: ErrorCode) : DataLatLon()
}
