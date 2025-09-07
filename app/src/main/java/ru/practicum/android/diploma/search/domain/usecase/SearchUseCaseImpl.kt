package ru.practicum.android.diploma.search.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.search.domain.model.VacanciesPage
import ru.practicum.android.diploma.search.domain.repository.VacancyRepository

class SearchUseCaseImpl(
    private val vacancyRepository: VacancyRepository
) : SearchUseCase {
    override fun searchVacancies(query: String, page: Int): Flow<Resource<VacanciesPage>> {
        return vacancyRepository.searchVacancies(query, page)
    }
}
