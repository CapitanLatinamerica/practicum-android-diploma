package ru.practicum.android.diploma.filtercountry.ui

import ru.practicum.android.diploma.common.domain.entity.Area

sealed interface CountryState {
    data object Error : CountryState
    data class Content(val countries: List<Area>) : CountryState
}
