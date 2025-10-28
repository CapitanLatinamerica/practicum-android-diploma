package ru.practicum.android.diploma.filterworkplace.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.filtersettings.data.FilterParameters
import ru.practicum.android.diploma.filtersettings.domain.FilteringUseCase

class WorkplaceViewModel(
    private val filteringUseCase: FilteringUseCase
) : ViewModel() {
    private val _workplaceState = MutableLiveData(WorkplaceState())
    val workplaceState: LiveData<WorkplaceState> = _workplaceState

    init {
        loadExistingFilterSettings()
    }

    fun loadExistingFilterSettings() {
        viewModelScope.launch {
            val existingParams = filteringUseCase.loadParameters()
            existingParams?.let { params ->
                _workplaceState.postValue(
                    WorkplaceState(country = params.country, region = params.region)
                )
            }
        }
    }

    fun clearCountry() {
        viewModelScope.launch {
            val currentParams = filteringUseCase.loadParameters() ?: FilterParameters()
            val updatedParams = currentParams.copy(country = "", countryId = 0)
            filteringUseCase.saveParameters(updatedParams)
            _workplaceState.value = _workplaceState.value?.copy(
                country = "",
            )
        }
    }

    fun clearRegion() {
        viewModelScope.launch {
            val currentParams = filteringUseCase.loadParameters() ?: FilterParameters()
            val updatedParams = currentParams.copy(region = "", regionId = 0)
            filteringUseCase.saveParameters(updatedParams)
            _workplaceState.value = _workplaceState.value?.copy(region = "")
        }
    }
}
