package ru.practicum.android.diploma.favourites.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.favourites.domain.db.FavouritesInteractor

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
                runCatching {
                    if (favouriteList.isEmpty()) {
                        _favouritesState.value = FavouritesState.Empty
                    } else {
                        _favouritesState.value = FavouritesState.Content(favouriteList)
                    }
                }.onFailure {
                    _favouritesState.value = FavouritesState.Error
                }
            }
        }
    }
}
