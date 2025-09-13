package ru.practicum.android.diploma.filterworkplace.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class WorkplaceViewModel : ViewModel() {
    private val _workplaceState = MutableLiveData(WorkplaceState())
    val workplaceState: LiveData<WorkplaceState> = _workplaceState

    private var initialState: WorkplaceState = WorkplaceState()

    val buttonsVisibilityState: LiveData<Boolean> = _workplaceState.map { current ->
        current != initialState
    }

    fun onCountrySelected(value: String) {
        _workplaceState.value = _workplaceState.value?.copy(country = value)
    }

    fun onRegionSelected(value: String) {
        _workplaceState.value = _workplaceState.value?.copy(region = value)
    }

    fun clearCountry() {
        _workplaceState.value = _workplaceState.value?.copy(country = "")
    }

    fun clearRegion() {
        _workplaceState.value = _workplaceState.value?.copy(region = "")
    }
}
