package ru.practicum.android.diploma.favourites.ui

import ru.practicum.android.diploma.search.ui.model.VacancyUi

sealed interface FavouritesState {

    data object Empty : FavouritesState
    data object Error : FavouritesState
    data class Content(val favouritesList: List<VacancyUi>) : FavouritesState
}
