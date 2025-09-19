package ru.practicum.android.diploma.filtersettings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.filtersettings.data.FilterParameters
import ru.practicum.android.diploma.filtersettings.domain.FilteringUseCase
import ru.practicum.android.diploma.filtersettings.ui.mapper.FilterParametersMapper

class FilteringViewModel(
    private val filteringUseCase: FilteringUseCase,
    private val mapper: FilterParametersMapper
) : ViewModel() {

    private val _filterState = MutableLiveData(FilterState())
    val filterState: LiveData<FilterState> = _filterState

    private val _buttonsVisibilityState = MutableLiveData(false)
    val buttonsVisibilityState: LiveData<Boolean> = _buttonsVisibilityState

    private var initialState: FilterState = FilterState()

    private var initialized = false

    private fun isStateNotBlank(state: FilterState): Boolean {
        return state.onlyWithSalary ||
            state.country.isNotBlank() ||
            state.industry.isNotBlank() ||
            state.salary.isNotBlank() ||
            state.region.isNotBlank()
    }

    init {
        viewModelScope.launch {
            val savedParams = filteringUseCase.loadParameters()
            val starting = if (savedParams != null) mapper.mapParamsToUi(savedParams) else FilterState()
            initialState = starting

            _filterState.postValue(starting)
            initialized = true
        }
    }

    fun onSalaryTextChanged(text: String) {
        updateSalaryAndCheckbox { currentState ->
            currentState.copy(salary = text)
        }
    }

    fun onOnlyWithSalaryToggled(isChecked: Boolean) {
        updateSalaryAndCheckbox { currentState ->
            currentState.copy(onlyWithSalary = isChecked)
        }
    }

    fun clearWorkplace() {
        viewModelScope.launch {
            val currentParams = filteringUseCase.loadParameters() ?: FilterParameters()
            val updatedParams = currentParams.copy(
                country = "",
                countryId = 0,
                region = "",
                regionId = 0
            )
            filteringUseCase.saveParameters(updatedParams)

            val currentState = _filterState.value ?: FilterState()
            val newState = currentState.copy(country = "", region = "")
            _filterState.value = newState
            updateButtonsVisibility(newState)
        }
    }

    fun clearIndustry() {
        viewModelScope.launch {
            val currentParams = filteringUseCase.loadParameters() ?: FilterParameters()
            val updatedParams = currentParams.copy(industry = "", industryId = 0)
            filteringUseCase.saveParameters(updatedParams)

            val currentState = _filterState.value ?: FilterState()
            val newState = currentState.copy(industry = "")
            _filterState.value = newState
            updateButtonsVisibility(newState)
        }
    }

    fun loadFilterSettings() {
        viewModelScope.launch {
            val savedParams = filteringUseCase.loadParameters()
            val newState = if (savedParams != null) mapper.mapParamsToUi(savedParams) else FilterState()

            if (!initialized) {
                initialState = newState
                initialized = true
            }

            _filterState.postValue(newState)
            updateButtonsVisibility(newState)
        }
    }

    private fun updateSalaryAndCheckbox(transform: (FilterState) -> FilterState) {
        val currentState = _filterState.value ?: FilterState()
        val newState = transform(currentState)

        if (newState == currentState) return
        _filterState.value = newState

        if (!initialized) return

        updateButtonsVisibility(newState)

        viewModelScope.launch {
            val currentParams = filteringUseCase.loadParameters() ?: FilterParameters()

            val updatedParams = currentParams.copy(
                salary = newState.salary,
                onlyWithSalary = newState.onlyWithSalary
            )

            filteringUseCase.saveParameters(updatedParams)
        }
    }

    fun clearAllParams() {
        val newParams = FilterState()
        _filterState.value = newParams

        viewModelScope.launch {
            filteringUseCase.clearParameters()
        }
        updateButtonsVisibility(newParams)
    }

    fun checkEmptyStorage() {
        viewModelScope.launch {
            val isStorageEmpty = filteringUseCase.isNotBlank()
            _buttonsVisibilityState.postValue(isStorageEmpty)
        }
    }

    private fun updateButtonsVisibility(state: FilterState) {
        _buttonsVisibilityState.value = isStateNotBlank(state)
    }
}
