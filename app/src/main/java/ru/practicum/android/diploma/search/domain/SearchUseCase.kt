package ru.practicum.android.diploma.search.domain

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.domain.entity.FilteredVacancyParameters
import ru.practicum.android.diploma.search.domain.model.VacanciesPage

interface SearchUseCase {
    fun searchVacancies(filteredVacancyParameters: FilteredVacancyParameters): Flow<Resource<VacanciesPage>>
}
