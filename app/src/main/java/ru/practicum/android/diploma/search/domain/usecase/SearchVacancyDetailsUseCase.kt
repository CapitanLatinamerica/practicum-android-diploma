package ru.practicum.android.diploma.search.domain.usecase

import ru.practicum.android.diploma.common.domain.entity.Vacancy

interface SearchVacancyDetailsUseCase {
    fun getVacancyById(id: String): Vacancy
}
