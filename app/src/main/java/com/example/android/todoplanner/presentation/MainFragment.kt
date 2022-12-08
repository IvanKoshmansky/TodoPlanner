package com.example.android.todoplanner.presentation

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.todoplanner.R
import com.example.android.todoplanner.TodoApplication
import com.example.android.todoplanner.databinding.FragmentMainBinding
import com.example.android.todoplanner.di.ViewModelFactory
import com.example.android.todoplanner.presentation.adapters.EventItemClickListener
import com.example.android.todoplanner.presentation.adapters.EventsListAdapter
import com.example.android.todoplanner.presentation.viewmodels.MainViewModel
import javax.inject.Inject

class MainFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var _mainViewModel: MainViewModel? = null
    private val mainViewModel: MainViewModel
        get() = _mainViewModel!!

    private var _eventsAdapter: EventsListAdapter? = null
    private val eventsAdapter: EventsListAdapter
        get() = _eventsAdapter!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (getContext()?.applicationContext as TodoApplication).appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _mainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // получить объект биндинга, привязать lifecycleOwner для корректной работы с LiveData
        val binding: FragmentMainBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main,
            container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.mainViewModel = mainViewModel

        _eventsAdapter = EventsListAdapter(EventItemClickListener { uid ->
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToDetailsFragment(uid)
            )
        })
        binding.rvEvents.adapter = _eventsAdapter

        mainViewModel.setupViewData()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.eventsData.observe(viewLifecycleOwner) {
            eventsAdapter.submitList(it)
        }

        mainViewModel.navigateToEditNewEvent.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    findNavController().navigate(
                        MainFragmentDirections.actionMainFragmentToEditFragment(0, true)
                    )
                    mainViewModel.navigateToEditNewEventDone()
                }
            }
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.main_menu_delete_visited -> {
                        mainViewModel.deleteAllVisited()
                        true
                    }
                    R.id.main_menu_delete_missed -> {
                        mainViewModel.deleteAllMissed()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        // показывать меню только в состоянии RESUMED которое будет у viewLifecycleOwner

    }

}
