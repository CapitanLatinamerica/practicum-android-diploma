package ru.practicum.android.diploma

import ru.practicum.android.diploma.vacancydetails.domain.VacancyDetails

sealed interface VacancyDetailsResource {
    object Loading : VacancyDetailsResource
    data class Content(val vacancy: VacancyDetails) : VacancyDetailsResource
    data class Error(val message: String) : VacancyDetailsResource
}
