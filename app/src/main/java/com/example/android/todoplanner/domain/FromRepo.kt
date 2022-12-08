package com.example.android.todoplanner.domain

import com.example.android.todoplanner.ErrorCode

sealed class FromRepo<out T> {
    class DataOk<T> (val data: T) : FromRepo<T>()
    class DataError (val code: ErrorCode) : FromRepo<Nothing>()
}
