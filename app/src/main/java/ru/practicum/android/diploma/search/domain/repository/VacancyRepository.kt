package ru.practicum.android.diploma.search.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.domain.entity.Vacancy
import ru.practicum.android.diploma.search.domain.model.VacanciesPage

interface VacancyRepository {
    fun searchVacancies(query: String, page: Int): Flow<Resource<VacanciesPage>>
    suspend fun getVacancyDetailsById(id: String) : Vacancy
}
