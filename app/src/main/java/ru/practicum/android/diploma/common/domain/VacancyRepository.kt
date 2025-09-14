package ru.practicum.android.diploma.common.domain

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.data.domain.api.AreaDto
import ru.practicum.android.diploma.common.domain.entity.FilteredVacancyParameters
import ru.practicum.android.diploma.common.domain.entity.Vacancy
import ru.practicum.android.diploma.search.domain.model.VacanciesPage

interface VacancyRepository {

    // Поиск вакансий с фильтрацией по тексту и страницам
    fun searchVacancies(query: String, page: Int): Flow<Resource<VacanciesPage>>

    fun searchAllVacancies(): List<Vacancy>

    fun searchVacanciesWithFilter(filteredVacancyParameters: FilteredVacancyParameters): List<Vacancy>

    // Получение детальной информации о вакансии по ID, возвращает Resource с Vacancy
    suspend fun getVacancyDetailsById(id: String): Resource<Vacancy>

    fun deleteById(id: String): Boolean

    fun deleteVacancyFromFavorites(id: String)

}
