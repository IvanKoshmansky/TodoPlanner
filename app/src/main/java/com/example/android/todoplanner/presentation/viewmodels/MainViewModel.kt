package com.example.android.todoplanner.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.todoplanner.domain.FromRepo
import com.example.android.todoplanner.domain.Repository
import com.example.android.todoplanner.presentation.model.ViewEventShortInfo
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor (private val repository: Repository) : ViewModel() {

    private val _eventsData = MutableLiveData<List<ViewEventShortInfo>>()
    val eventsData: LiveData<List<ViewEventShortInfo>>
        get() = _eventsData

    private suspend fun fetchAllEvents() {
        when (val eventsFromRepo = repository.getAllEvents()) {
            is FromRepo.DataOk -> {
                val listOfShortEvents = eventsFromRepo.data.map {
                    ViewEventShortInfo(
                        uid = it.uid,
                        name = it.name,
                        dateTime = it.dateTime,
                        status = it.status
                    )
                }
                _eventsData.postValue(listOfShortEvents)
            }
            is FromRepo.DataError -> {
                _eventsData.postValue(listOf())
            }
        }
    }

    fun setupViewData() {
        viewModelScope.launch {
            repository.updateAllEventsStatus()
            fetchAllEvents()
        }
    }

    private val _navigateToEditNewEvent = MutableLiveData<Boolean>()
    val navigateToEditNewEvent: LiveData<Boolean>
        get() = _navigateToEditNewEvent

    fun onAddNewEvent() {
        _navigateToEditNewEvent.value = true
    }

    fun navigateToEditNewEventDone() {
        _navigateToEditNewEvent.value = false
    }

    fun deleteAllVisited() {
        viewModelScope.launch {
            repository.deleteAllVisitedEvents()
            fetchAllEvents()
        }
    }

    fun deleteAllMissed() {
        viewModelScope.launch {
            repository.deleteAllMissedEvents()
            fetchAllEvents()
        }
    }

}
