package ru.practicum.android.diploma.vacancydetails.domain

sealed interface VacancyDetailsState {
    object Loading : VacancyDetailsState
    data class Content(val vacancy: VacancyDetails) : VacancyDetailsState
    data class Error(val message: String) : VacancyDetailsState
}
