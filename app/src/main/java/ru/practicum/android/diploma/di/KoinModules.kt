package ru.practicum.android.diploma.di

import androidx.room.Room
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.practicum.android.diploma.vacancydetails.data.VacancyDetailsRepositoryImpl
import ru.practicum.android.diploma.vacancydetails.domain.VacancyDetailsRepository
import ru.practicum.android.diploma.vacancydetails.ui.VacancyDetailsViewModel
import ru.practicum.android.diploma.common.data.db.AppDataBase
import ru.practicum.android.diploma.common.data.db.FavouritesRepositoryImpl
import ru.practicum.android.diploma.common.data.mapper.VacancyMapper
import ru.practicum.android.diploma.common.domain.db.FavouritesInteractor
import ru.practicum.android.diploma.common.domain.db.FavouritesRepository
import ru.practicum.android.diploma.common.domain.impl.FavouritesInteractorImpl

// Общие зависимости
val appModule = module {

}

// Модуль для работы с Room
val databaseModule = module {
    factory { VacancyMapper }

    single {
        Room.databaseBuilder(androidContext(), AppDataBase::class.java, "database.db")
            .fallbackToDestructiveMigration(false)
            .build()
    }
}

// Модуль для работы с Room
val searchModule = module {

}

// Модуль для деталей вакансии
val vacancyDetailsModule = module {

    viewModel { (vacancyId: String) ->
        VacancyDetailsViewModel(get(), vacancyId)
    }
    single<VacancyDetailsRepository> {
        VacancyDetailsRepositoryImpl()
    }
}

val favouritesModule = module {

    single<FavouritesInteractor> {
        FavouritesInteractorImpl(get())
    }
    single<FavouritesRepository> {
        FavouritesRepositoryImpl(get(), get())
    }
}
