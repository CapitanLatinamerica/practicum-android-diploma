package ru.practicum.android.diploma.filtercountry.ui

import ru.practicum.android.diploma.common.domain.entity.Area

sealed interface CountryState {
    data object Loading : CountryState
    data class Error(val message: String) : CountryState
    data class Content(val countries: List<Area>) : CountryState
}
