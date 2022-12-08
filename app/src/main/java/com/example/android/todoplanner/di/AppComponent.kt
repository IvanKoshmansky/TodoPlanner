package com.example.android.todoplanner.di

import com.example.android.todoplanner.TodoApplication
import com.example.android.todoplanner.presentation.DetailsFragment
import com.example.android.todoplanner.presentation.EditFragment
import com.example.android.todoplanner.presentation.MainActivity
import com.example.android.todoplanner.presentation.MainFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [ViewModelFactoryModule::class, ViewModelsModule::class, DataModule::class])
@Singleton
interface AppComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(mainFragment: MainFragment)
    fun inject(editFragment: EditFragment)
    fun inject(detailsFragment: DetailsFragment)
//    fun inject(application: TodoApplication)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun applicationContext(context: TodoApplication): Builder
        fun build(): AppComponent
    }
}

//@Component.Builder или @Component.Factory используется для создания AppComponent в Application:
//val appComponent = DaggerAppComponent
//    .builder()
//    .applicationContext(this)
//    .build()
//@BindsInstance используется вместо @Binds для передачи контекста,
// поскольку конструктор контекста Android в явном виде недоступен
