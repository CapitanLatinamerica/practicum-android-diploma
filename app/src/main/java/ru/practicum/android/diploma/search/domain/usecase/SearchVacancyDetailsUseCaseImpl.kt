package ru.practicum.android.diploma.search.domain.usecase

import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.domain.VacancyRepository
import ru.practicum.android.diploma.common.domain.entity.Vacancy

class SearchVacancyDetailsUseCaseImpl(
    private val vacancyRepository: VacancyRepository
) : SearchVacancyDetailsUseCase {
    override suspend fun getVacancyDetailsById(id: String): Resource<Vacancy> {
        return vacancyRepository.getVacancyDetailsById(id)
    }
}
