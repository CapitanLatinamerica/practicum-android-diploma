package ru.practicum.android.diploma.vacancydetails.domain

interface VacancyDetailsRepository {
    suspend fun getVacancyDetails(vacancyId: String): VacancyDetails
    suspend fun addToFavorites(vacancy: VacancyDetails)
    suspend fun removeFromFavorites(vacancyId: String)
    suspend fun isVacancyFavorite(vacancyId: String): Boolean
}
