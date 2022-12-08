package com.example.android.todoplanner.di

import androidx.lifecycle.ViewModel
import com.example.android.todoplanner.presentation.viewmodels.DetailsViewModel
import com.example.android.todoplanner.presentation.viewmodels.EditViewModel
import com.example.android.todoplanner.presentation.viewmodels.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditViewModel::class)
    abstract fun bindEditViewModel(editViewModel: EditViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DetailsViewModel::class)
    abstract fun bindDetailsViewModel(detailsViewModel: DetailsViewModel): ViewModel

}
