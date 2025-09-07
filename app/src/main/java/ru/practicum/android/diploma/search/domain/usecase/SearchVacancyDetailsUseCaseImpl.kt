package ru.practicum.android.diploma.search.domain.usecase

import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.domain.entity.Vacancy
import ru.practicum.android.diploma.search.domain.repository.VacancyRepository

class SearchVacancyDetailsUseCaseImpl(
    private val vacancyRepository: VacancyRepository
) : SearchVacancyDetailsUseCase {
    override suspend fun getVacancyById(id: String): Vacancy? {
        val resource = vacancyRepository.getVacancyDetailsById(id)
        when (resource){
            is Resource.Success<Vacancy> -> return resource.data
            is Resource.Error<*> -> return null
        }

    }
}
