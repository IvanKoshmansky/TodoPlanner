package com.example.android.todoplanner.presentation

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import com.example.android.todoplanner.presentation.viewmodels.EditViewModel
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.todoplanner.R
import com.example.android.todoplanner.Status
import com.example.android.todoplanner.TodoApplication
import com.example.android.todoplanner.databinding.FragmentEditBinding
import com.example.android.todoplanner.di.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import javax.inject.Inject

class EditFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var _editViewModel: EditViewModel? = null
    private val editViewModel: EditViewModel
        get() = _editViewModel!!

    private var _binding: FragmentEditBinding? = null
    private val binding: FragmentEditBinding
        get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (getContext()?.applicationContext as TodoApplication).appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _editViewModel = ViewModelProvider(this, viewModelFactory).get(EditViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.editViewModel = editViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            val arguments = EditFragmentArgs.fromBundle(requireArguments())
            if (arguments.newEvent) {
                editViewModel.setupDefaultView()
            } else {
                editViewModel.fetchEventWithWeather(arguments.eventUid)
            }
        }

        editViewModel.showDatePickerLiveData.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    val picker = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener {
                        _, year: Int, month: Int, dayOfMonth: Int ->
                            editViewModel.setDateFromPicker(year, month, dayOfMonth)
                        },
                        editViewModel.pickerDate.year,
                        editViewModel.pickerDate.month,
                        editViewModel.pickerDate.day
                    )
                    picker.show()
                    editViewModel.showDatePickerLiveData.value = false
                }
            }
        }

        editViewModel.showTimePickerLiveData.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    val picker = TimePickerDialog(requireContext(), TimePickerDialog.OnTimeSetListener {
                            _, hourOfDay, minute -> editViewModel.setTimeFromPicker(hourOfDay, minute)
                        },
                        editViewModel.pickerTime.hour,
                        editViewModel.pickerTime.minute,
                        true
                    )
                    picker.show()
                    editViewModel.showTimePickerLiveData.value = false
                }
            }
        }

        editViewModel.weatherViewState.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    Status.LOADING -> {
                        binding.iwWeatherIcon.setImageResource(R.drawable.ic_sync_48)
                        binding.twWeatherDesc.text = ""
                        binding.twWeatherTemp.setText(R.string.loading_process_updating)
                    }
                    Status.OK -> {
                        Picasso.get().load(editViewModel.weatherInfo.iconUrl)
                            .placeholder(R.drawable.ic_sync_48)
                            .error(R.drawable.ic_draft_48)
                            .into(binding.iwWeatherIcon)
                        binding.twWeatherDesc.text = editViewModel.weatherInfo.description
                        binding.twWeatherTemp.text = "${editViewModel.weatherInfo.temp} °C"
                    }
                    Status.UNAVAILABLE -> {
                        binding.iwWeatherIcon.setImageResource(R.drawable.ic_draft_48)
                        binding.twWeatherDesc.text = ""
                        binding.twWeatherTemp.setText(R.string.loading_process_unavailable)
                    }
                    else -> {}
                }
            }
        }

        editViewModel.saveNewEventStatusLiveData.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    Status.OK -> {
                        Snackbar.make(binding.root, R.string.snackbar_saved, Snackbar.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                        editViewModel.saveNewEventStatusLiveData.value = Status.UNKNOWN
                    }
                    Status.EMPTY -> {
                        Snackbar.make(binding.root, R.string.snackbar_need_to_fill_name, Snackbar.LENGTH_SHORT).show()
                        editViewModel.saveNewEventStatusLiveData.value = Status.UNKNOWN
                    }
                    else -> {}
                }
            }
        }
    }

}
