package ru.practicum.android.diploma.favourites.ui

import ru.practicum.android.diploma.common.domain.entity.Vacancy

sealed interface FavouritesState {

    data object Empty : FavouritesState
    data object Error : FavouritesState
    data class Content(val favouritesList: List<Vacancy>) : FavouritesState
}
