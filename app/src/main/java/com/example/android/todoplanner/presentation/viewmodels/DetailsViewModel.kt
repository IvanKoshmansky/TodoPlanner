package com.example.android.todoplanner.presentation.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.todoplanner.Status
import com.example.android.todoplanner.domain.EventStatus
import com.example.android.todoplanner.domain.FromRepo
import com.example.android.todoplanner.domain.Repository
import com.example.android.todoplanner.domain.model.EventInfo
import com.example.android.todoplanner.domain.model.WeatherInfo
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailsViewModel @Inject constructor (private val repository: Repository) : ViewModel() {

    private val _eventInfoLiveData = MutableLiveData(EventInfo.fillEmpty())
    val eventInfo: LiveData<EventInfo>
        get() = _eventInfoLiveData

    private var _weatherInfo = WeatherInfo.fillEmpty()
    val weatherInfo: WeatherInfo
        get() = _weatherInfo

    private val _weatherViewState = MutableLiveData(Status.EMPTY)
    val weatherViewState: LiveData<Status>
        get() = _weatherViewState

    val btnSetVisitedVisibilityLiveData = MutableLiveData(View.GONE)

    fun setupViewData(uid: Long) {
        viewModelScope.launch {
            when (val event = repository.getEventById(uid)) {
                is FromRepo.DataOk -> {
                    _eventInfoLiveData.postValue(event.data)
                    if (event.data.status == EventStatus.ACTIVE) {
                        btnSetVisitedVisibilityLiveData.postValue(View.VISIBLE)
                    } else {
                        btnSetVisitedVisibilityLiveData.postValue(View.GONE)
                    }
                    _weatherViewState.postValue(Status.LOADING)
                    when (val weather = repository.getWeather(event.data.place, event.data.dateTime)) {
                        is FromRepo.DataOk -> {
                            _weatherInfo = weather.data
                            _weatherViewState.postValue(Status.OK)
                        }
                        is FromRepo.DataError -> {
                            _weatherViewState.postValue(Status.UNAVAILABLE)
                        }
                    }
                }
                is FromRepo.DataError -> {}
            }
        }
    }

    private val _navigateToEditLiveData = MutableLiveData(false)
    val navigateToEditLiveData: LiveData<Boolean>
        get() = _navigateToEditLiveData

    fun onBeginEdit() {
        _navigateToEditLiveData.value = true
    }

    fun navigationToEditDone() {
        _navigateToEditLiveData.value = false
    }

    val setVisitedLiveData = MutableLiveData(Status.UNKNOWN)
    val deletedLiveData = MutableLiveData(Status.UNKNOWN)

    fun onSetVisited() {
        val uid = _eventInfoLiveData.value?.uid
        uid?.let {
            viewModelScope.launch {
                repository.updateEventStatus(it, EventStatus.VISITED)
                when (val changed = repository.getEventById(it)) {
                    is FromRepo.DataOk -> {
                        if (changed.data.status == EventStatus.VISITED) {
                            _eventInfoLiveData.postValue(changed.data)
                            setVisitedLiveData.postValue(Status.OK)
                        }
                    }
                    is FromRepo.DataError -> {
                        setVisitedLiveData.postValue(Status.ERROR)
                    }
                }
            }
        }
    }

    fun onDelete() {
        _eventInfoLiveData.value?.uid?.let {
            viewModelScope.launch {
                repository.deleteEventById(it)
                deletedLiveData.postValue(Status.OK)
            }
        }
    }
}
