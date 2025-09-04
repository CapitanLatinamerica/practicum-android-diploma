package ru.practicum.android.diploma.common.domain

import ru.practicum.android.diploma.common.data.domain.api.Area
import ru.practicum.android.diploma.common.domain.entity.FilteredVacancyParameters
import ru.practicum.android.diploma.common.domain.entity.Industry
import ru.practicum.android.diploma.common.domain.entity.Vacancy

interface VacancyRepository {

    fun searchAllVacancies(): List<Vacancy>

    fun searchVacanciesWithFilter(filteredVacancyParameters: FilteredVacancyParameters): List<Vacancy>

    fun getVacancyById(id: String)

    fun deleteById(id: String): Boolean

    fun getIndustries(): List<Industry>

    fun getAreas(): List<Area>

    fun addVacancyToFavorites(vacancyList: List<Vacancy>)

    fun deleteVacancyFromFavorites(id: String)

}
