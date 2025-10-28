package ru.practicum.android.diploma.search.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.domain.VacancyRepository
import ru.practicum.android.diploma.common.domain.entity.FilteredVacancyParameters
import ru.practicum.android.diploma.search.domain.SearchUseCase
import ru.practicum.android.diploma.search.domain.model.VacanciesPage

class SearchUseCaseImpl(
    private val vacancyRepository: VacancyRepository
) : SearchUseCase {
    override fun searchVacancies(filteredVacancyParameters: FilteredVacancyParameters): Flow<Resource<VacanciesPage>> {
        return vacancyRepository.searchVacancies(filteredVacancyParameters)
    }
}
