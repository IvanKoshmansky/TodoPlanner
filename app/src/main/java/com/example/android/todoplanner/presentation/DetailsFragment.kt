package com.example.android.todoplanner.presentation

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
import com.example.android.todoplanner.databinding.FragmentDetailsBinding
import com.example.android.todoplanner.di.ViewModelFactory
import com.example.android.todoplanner.presentation.viewmodels.DetailsViewModel
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import javax.inject.Inject

class DetailsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var _detailsViewModel: DetailsViewModel? = null
    private val detailsViewModel: DetailsViewModel
        get() = _detailsViewModel!!

    private var _binding: FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding
        get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (getContext()?.applicationContext as TodoApplication).appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _detailsViewModel = ViewModelProvider(this, viewModelFactory).get(DetailsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.detailsViewModel = detailsViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            val arguments = DetailsFragmentArgs.fromBundle(requireArguments())
            detailsViewModel.setupViewData(arguments.eventUid)
        }

        detailsViewModel.weatherViewState.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    Status.LOADING -> {
                        binding.iwEventWeatherIcon.setImageResource(R.drawable.ic_sync_48)
                        binding.twWeatherDescription.text = ""
                        binding.twWeatherTemp.setText(R.string.loading_process_updating)
                    }
                    Status.OK -> {
                        Picasso.get().load(detailsViewModel.weatherInfo.iconUrl)
                            .placeholder(R.drawable.ic_sync_48)
                            .error(R.drawable.ic_draft_48)
                            .into(binding.iwEventWeatherIcon)
                        binding.twWeatherDescription.text = detailsViewModel.weatherInfo.description
                        binding.twWeatherTemp.text = "${detailsViewModel.weatherInfo.temp} °C"
                    }
                    Status.UNAVAILABLE -> {
                        binding.iwEventWeatherIcon.setImageResource(R.drawable.ic_draft_48)
                        binding.twWeatherDescription.text = ""
                        binding.twWeatherTemp.setText(R.string.loading_process_unavailable)
                    }
                    else -> {}
                }
            }
        }

        detailsViewModel.setVisitedLiveData.observe(viewLifecycleOwner) {
            it.let {
                if (it == Status.OK) {
                    Snackbar.make(binding.root, R.string.snackbar_saved, Snackbar.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                    detailsViewModel.setVisitedLiveData.value = Status.UNKNOWN
                }
            }
        }

        detailsViewModel.deletedLiveData.observe(viewLifecycleOwner) {
            it.let {
                if (it == Status.OK) {
                    Snackbar.make(binding.root, R.string.snackbar_deleted, Snackbar.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                    detailsViewModel.deletedLiveData.value = Status.UNKNOWN
                }
            }
        }

        detailsViewModel.navigateToEditLiveData.observe(viewLifecycleOwner) {
            it.let {
                if (it) {
                    val uid = detailsViewModel.eventInfo.value?.uid
                    findNavController().navigate(
                        DetailsFragmentDirections.actionDetailsFragmentToEditFragment(uid!!, false)
                    )
                    detailsViewModel.navigationToEditDone()
                }
            }
        }
    }

}
