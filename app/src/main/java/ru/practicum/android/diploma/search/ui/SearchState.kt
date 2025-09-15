package ru.practicum.android.diploma.search.ui

import ru.practicum.android.diploma.ErrorType
import ru.practicum.android.diploma.search.ui.model.VacancyUi

sealed interface SearchState {
    data class Initial(val isFilterParametersApplied: Boolean) : SearchState
    data object Loading : SearchState
    data class Content(val found: Int, val vacancies: List<VacancyUi>) : SearchState
    data class Empty(val message: String) : SearchState
    data class Error(val errorType: ErrorType, val errorMessage: String) : SearchState
}
