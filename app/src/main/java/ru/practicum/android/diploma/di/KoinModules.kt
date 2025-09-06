package ru.practicum.android.diploma.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.vacancydetails.data.VacancyDetailsRepositoryImpl
import ru.practicum.android.diploma.vacancydetails.domain.VacancyDetailsRepository
import ru.practicum.android.diploma.vacancydetails.ui.VacancyDetailsViewModel

// Общие зависимости
val appModule = module {

}

// Модуль для работы с Room
val databaseModule = module {

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
        VacancyDetailsRepositoryImpl() // Пока заглушка
    }
}
