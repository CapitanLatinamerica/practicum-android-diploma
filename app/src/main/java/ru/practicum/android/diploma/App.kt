package ru.practicum.android.diploma

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.practicum.android.diploma.di.appModule
import ru.practicum.android.diploma.di.databaseModule
import ru.practicum.android.diploma.di.searchModule

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(appModule, databaseModule, searchModule)
        }

    }
}
