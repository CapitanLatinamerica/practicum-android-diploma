package ru.practicum.android.diploma.vacancydetails.domain

interface VacancyDetailsRepository {
    suspend fun getVacancyDetails(vacancyId: String): VacancyDetails
}
