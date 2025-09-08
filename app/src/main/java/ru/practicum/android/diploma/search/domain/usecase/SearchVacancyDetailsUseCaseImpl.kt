package ru.practicum.android.diploma.search.domain.usecase

import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.domain.VacancyRepository
import ru.practicum.android.diploma.common.domain.entity.Vacancy

class SearchVacancyDetailsUseCaseImpl(
    private val vacancyRepository: VacancyRepository
) : SearchVacancyDetailsUseCase {
    override suspend fun getVacancyById(id: String): Vacancy? {
        // Запрашивает у репозитория детали вакансии по ID
        val resource = vacancyRepository.getVacancyDetailsById(id)

        // Если результат успешен, возвращает данные, иначе null
        return when (resource) {
            is Resource.Success<Vacancy> -> resource.data
            is Resource.Error<*> -> null
        }
    }
}
