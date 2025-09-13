package ru.practicum.android.diploma

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.practicum.android.diploma.di.databaseModule
import ru.practicum.android.diploma.di.favouritesModule
import ru.practicum.android.diploma.di.filteringModule
import ru.practicum.android.diploma.di.industryModule
import ru.practicum.android.diploma.di.searchModule
import ru.practicum.android.diploma.di.vacancyDetailsModule
import ru.practicum.android.diploma.di.workplaceModule

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                databaseModule,
                searchModule,
                vacancyDetailsModule,
                favouritesModule,
                filteringModule,
                industryModule,
                workplaceModule,
                // appModule
            )
        }
    }
}
