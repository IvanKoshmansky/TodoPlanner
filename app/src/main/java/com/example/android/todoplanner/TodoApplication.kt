package com.example.android.todoplanner

import android.app.Application
import com.example.android.todoplanner.di.DaggerAppComponent

class TodoApplication : Application() {

    val appComponent = DaggerAppComponent
        .builder()
        .applicationContext(this)
        .build()

}
