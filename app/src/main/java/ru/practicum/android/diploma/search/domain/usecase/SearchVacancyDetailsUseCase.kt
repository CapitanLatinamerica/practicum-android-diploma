package ru.practicum.android.diploma.search.domain.usecase

import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.domain.entity.Vacancy

interface SearchVacancyDetailsUseCase {
    suspend fun getVacancyDetailsById(id: String): Resource<Vacancy>
}
