package ru.practicum.android.diploma.search.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.search.domain.model.VacanciesPage

interface SearchUseCase {
    fun searchVacancies(query: String, page: Int = 0): Flow<Resource<VacanciesPage>>
}
