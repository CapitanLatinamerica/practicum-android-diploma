package ru.practicum.android.diploma.filtercountry.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.domain.entity.Area
import ru.practicum.android.diploma.filtercountry.domain.CountryInteractor
import ru.practicum.android.diploma.filtersettings.data.FilterParameters
import ru.practicum.android.diploma.filtersettings.domain.FilteringUseCase

class CountryViewModel(
    private val countryInteractor: CountryInteractor,
    private val filteringUseCase: FilteringUseCase
) : ViewModel() {

    private val _countryState = MutableLiveData<CountryState>()
    val countryState: LiveData<CountryState> = _countryState

    fun getCountries() {
        viewModelScope.launch {
            _countryState.value = CountryState.Loading
            val result = countryInteractor.getCountries()
            when (result) {
                is Resource.Success -> {
                    val countries = result.data
                    _countryState.value = countries?.let { CountryState.Content(it) }
                }

                is Resource.Error -> {
                    _countryState.value = CountryState.Error(result.message ?: "Неизвестная ошибка")
                }
            }
        }
    }

    fun saveCountry(selectedCounty: Area) {
        viewModelScope.launch {
            val currentParams = filteringUseCase.loadParameters() ?: FilterParameters()
            val updatedParams = currentParams.copy(
                country = selectedCounty.name,
                countryId = selectedCounty.id
            )
            filteringUseCase.saveParameters(updatedParams)
            _countryState.value = CountryState.CountrySelected
        }
    }
}
