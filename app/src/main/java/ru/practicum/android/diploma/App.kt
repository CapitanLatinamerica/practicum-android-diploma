package ru.practicum.android.diploma

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.practicum.android.diploma.di.appModule
import ru.practicum.android.diploma.di.databaseModule
import ru.practicum.android.diploma.di.favouritesModule
import ru.practicum.android.diploma.di.filteringModule
import ru.practicum.android.diploma.di.searchModule
import ru.practicum.android.diploma.di.vacancyDetailsModule

const val PREFERENCE_NAME = "user_preferences"

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(appModule, databaseModule, searchModule, vacancyDetailsModule, favouritesModule, filteringModule)
        }
    }
}
