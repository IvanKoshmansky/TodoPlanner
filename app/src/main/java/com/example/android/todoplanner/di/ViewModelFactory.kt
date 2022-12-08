package com.example.android.todoplanner.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
@Suppress("UNCHECKED_CAST")
class ViewModelFactory @Inject constructor (
    private val viewModelsMap: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>) :

    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator = viewModelsMap[modelClass] ?: throw IllegalArgumentException("unknown model class $modelClass")
        return try {
            creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

}

//Provider<ViewModel>: отложенная инициализация, при каждом обращении новый инстанс
//viewModelsMap: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>: коллекция зависимостей, мультибайндинг
//Class<T> маркер класса
