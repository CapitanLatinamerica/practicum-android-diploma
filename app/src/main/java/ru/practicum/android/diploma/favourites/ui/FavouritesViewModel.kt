package ru.practicum.android.diploma.favourites.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.common.domain.db.FavouritesInteractor

class FavouritesViewModel(private val favouritesInteractor: FavouritesInteractor) : ViewModel() {

    private val _favouritesState = MutableLiveData<FavouritesState>()
    val favouritesState: LiveData<FavouritesState> get() = _favouritesState


    init {
        viewModelScope.launch {
            getFavourites()
        }
    }

    private fun getFavourites() {
        viewModelScope.launch {
            favouritesInteractor.getAllVacancies().collect { favouriteList ->
                _favouritesState.value = if (favouriteList.isEmpty()) {
                    FavouritesState.Empty
                } else {
                    FavouritesState.Content(favouriteList)
                }
            }
        }
    }
}
