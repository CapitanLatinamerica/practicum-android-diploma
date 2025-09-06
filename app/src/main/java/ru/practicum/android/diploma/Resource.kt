package ru.practicum.android.diploma

import ru.practicum.android.diploma.vacancydetails.domain.VacancyDetails

sealed interface Resource {
    object Loading : Resource
    data class Content(val vacancy: VacancyDetails) : Resource
    data class Error(val message: String) : Resource
}
