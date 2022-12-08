package com.example.android.todoplanner.data.datasources

import com.example.android.todoplanner.data.model.DataLatLon

interface GeoCoderDataSource {
    suspend fun getLatLon(location: String): DataLatLon
}
