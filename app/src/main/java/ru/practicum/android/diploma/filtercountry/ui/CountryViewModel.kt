package ru.practicum.android.diploma.filtercountry.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.filtercountry.domain.CountryInteractor

class CountryViewModel(
    private val countryInteractor: CountryInteractor,
) : ViewModel() {

    private val _countryState = MutableLiveData<CountryState>()
    val countryState: LiveData<CountryState> = _countryState

    fun getCountries() {
        viewModelScope.launch {
                val result = countryInteractor.getCountries()
                when (result) {
                    is Resource.Success -> {
                        val countries = result.data
                        _countryState.value = CountryState.Content(countries!!)

                    }

                    is Resource.Error -> {
                        _countryState.value = CountryState.Error(result.message ?: "Неизвестная ошибка")
                    }
                }
        }
    }
}
