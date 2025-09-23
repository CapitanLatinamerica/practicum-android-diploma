package ru.practicum.android.diploma.vacancydetails.ui

import ru.practicum.android.diploma.ErrorType
import ru.practicum.android.diploma.common.domain.entity.Vacancy

sealed interface VacancyDetailsState {
    object Loading : VacancyDetailsState
    data class Content(val vacancy: Vacancy) : VacancyDetailsState
    data class Error(val errorType: ErrorType, val message: String) : VacancyDetailsState
}
