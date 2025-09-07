package ru.practicum.android.diploma.search.domain.usecase

import ru.practicum.android.diploma.common.domain.entity.Vacancy
import ru.practicum.android.diploma.search.domain.repository.VacancyRepository

class SearchVacancyDetailsUseCaseImpl(
    private val vacancyRepository: VacancyRepository
) : SearchVacancyDetailsUseCase {
    override fun getVacancyById(id: String): Vacancy {
        return vacancyRepository.
    }
}
