package com.example.android.todoplanner.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.todoplanner.Status
import com.example.android.todoplanner.domain.*
import com.example.android.todoplanner.domain.model.EventInfo
import com.example.android.todoplanner.domain.model.WeatherInfo
import com.example.android.todoplanner.getLocalDateTimeToString
import com.example.android.todoplanner.presentation.model.ShortDate
import com.example.android.todoplanner.presentation.model.ShortDateTime
import com.example.android.todoplanner.presentation.model.ShortTime
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class EditViewModel @Inject constructor (private val repository: Repository) : ViewModel() {

    private lateinit var _pickerDateTime: ShortDateTime
    val pickerDate: ShortDate
        get() = _pickerDateTime.date
    val pickerTime: ShortTime
        get() = _pickerDateTime.time

    init {
        setupPickerDateTime()
    }

    private fun setupPickerDateTime() {
        val cal = Calendar.getInstance(TimeZone.getDefault())
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        _pickerDateTime = ShortDateTime(ShortDate(year, month, day), ShortTime(hour, minute))
    }

    fun setDateFromPicker(year: Int, month: Int, day: Int) {
        _pickerDateTime.date = ShortDate(year, month, day)
        setDateTimeLiveDateFromPickerData()
    }

    fun setTimeFromPicker(hour: Int, minute: Int) {
        _pickerDateTime.time = ShortTime(hour, minute)
        setDateTimeLiveDateFromPickerData()
    }

    private fun setDateTimeLiveDateFromPickerData() {
        eventDateTimeLiveData.value = _pickerDateTime.toString()
    }

    val showDatePickerLiveData = MutableLiveData(false)
    val showTimePickerLiveData = MutableLiveData(false)

    fun onDatePickerShow() {
        showDatePickerLiveData.value = true
    }

    fun onTimePickerShow() {
        showTimePickerLiveData.value = true
    }

    private var currentUid: Long? = null
    private var currentEventStatus: EventStatus? = null

    val eventNameLiveData = MutableLiveData("")
    val eventDescLiveData = MutableLiveData("")
    val eventPlaceLiveData = MutableLiveData("")
    val eventDateTimeLiveData = MutableLiveData("")

    fun onSaveEvent() {
        if (eventNameLiveData.value != "") {
            val eventToSave = EventInfo.fillEmpty()
            eventToSave.name = eventNameLiveData.value ?: ""
            eventToSave.desc = eventDescLiveData.value ?: ""
            eventToSave.dateTime = eventDateTimeLiveData.value ?: ""
            eventToSave.place = eventPlaceLiveData.value ?: ""
            viewModelScope.launch {
                val currentTime = getLocalDateTimeToString()
                if (currentTime > eventDateTimeLiveData.value!!) {
                    eventToSave.status = EventStatus.MISSED
                } else {
                    eventToSave.status = EventStatus.ACTIVE
                }
                if (currentUid == null) {
                    eventToSave.uid = 0
                    repository.insertNewEvent(eventToSave)
                } else {
                    eventToSave.uid = currentUid!!
                    repository.updateEvent(eventToSave)
                }
                saveNewEventStatusLiveData.postValue(Status.OK)
            }
        } else {
            saveNewEventStatusLiveData.value = Status.EMPTY
        }
    }

    val saveNewEventStatusLiveData = MutableLiveData(Status.UNKNOWN)

    private var _weatherInfo = WeatherInfo.fillEmpty()
    val weatherInfo: WeatherInfo
        get() = _weatherInfo

    private val _weatherViewState = MutableLiveData(Status.EMPTY)
    val weatherViewState: LiveData<Status>
        get() = _weatherViewState

    private suspend fun fetchWeather(location: String, dateTime: String?) {
        dateTime?.let {
            _weatherViewState.postValue(Status.LOADING)
            when (val weather = repository.getWeather(location, dateTime)) {
                is FromRepo.DataOk -> {
                    _weatherInfo = weather.data
                    _weatherViewState.postValue(Status.OK)
                }
                is FromRepo.DataError -> {
                    _weatherViewState.postValue(Status.UNAVAILABLE)
                }
            }
        }
    }

    fun fetchEventWithWeather(uid: Long) {
        viewModelScope.launch {
            when (val eventInfo = repository.getEventById(uid)) {
                is FromRepo.DataOk -> {
                    eventNameLiveData.postValue(eventInfo.data.name)
                    eventDescLiveData.postValue(eventInfo.data.desc)
                    eventDateTimeLiveData.postValue(eventInfo.data.dateTime)
                    eventPlaceLiveData.postValue(eventInfo.data.place)
                    currentUid = eventInfo.data.uid
                    currentEventStatus = eventInfo.data.status
                    fetchWeather(eventInfo.data.place, eventInfo.data.dateTime)
                }
                is FromRepo.DataError-> {}
            }
        }
    }

    fun setupDefaultView() {
        setDateTimeLiveDateFromPickerData()
    }

    fun onWeatherUpdate() {
        val location = eventPlaceLiveData.value
        location?.let {
            viewModelScope.launch {
                fetchWeather(it, eventDateTimeLiveData.value)
            }
        }
    }

}
