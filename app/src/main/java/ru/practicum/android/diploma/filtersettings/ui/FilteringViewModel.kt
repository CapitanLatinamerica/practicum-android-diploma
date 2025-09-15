package ru.practicum.android.diploma.filtersettings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
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

    init {
        viewModelScope.launch {
            val savedParams = filteringUseCase.loadParameters()
            val starting = if (savedParams != null) mapper.mapParamsToUi(savedParams) else FilterState()
            initialState = starting

            _filterState.postValue(starting)
            _buttonsVisibilityState.postValue(starting != initialState)
            initialized = true
        }
    }
//    val selectedIndustryId: String?
//        get() = _filterState.value?.industry.takeIf { !it.isNullOrEmpty() }
//
//    fun onSalaryTextChanged(text: String) {
//        _filterState.value = _filterState.value?.copy(salary = text)
//    }
//
//    fun onOnlyWithSalaryToggled(isChecked: Boolean) {
//        _filterState.value = _filterState.value?.copy(onlyWithSalary = isChecked)
//    }

    fun onWorkplaceSelected(value: String) {
        updateAndSave { it.copy(country = value) }
    }

    fun onIndustrySelected(value: String) {
        updateAndSave { it.copy(industry = value) }
    }

    fun onSalaryTextChanged(text: String) {
        updateAndSave { it.copy(salary = text) }
    }

    fun onOnlyWithSalaryToggled(isChecked: Boolean) {
        updateAndSave { it.copy(onlyWithSalary = isChecked) }
    }

    fun clearWorkplace() {
        updateAndSave { it.copy(country = "") }
    }

    fun clearIndustry() {
        updateAndSave { it.copy(industry = "") }
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
            _buttonsVisibilityState.postValue(newState != initialState)
        }
    }

    private fun updateAndSave(transform: (FilterState) -> FilterState) {
        val currentState = _filterState.value ?: FilterState()
        val newState = transform(currentState)

        if (newState == currentState) return
        _filterState.value = newState

        if (!initialized) return

        _buttonsVisibilityState.postValue(newState != initialState)
        viewModelScope.launch {
            filteringUseCase.saveParameters(mapper.mapParamsToDomain(newState))
        }
    }

    fun clearAllParams() {
        val newParams = FilterState()
        _filterState.value = newParams

        viewModelScope.launch {
            filteringUseCase.clearParameters()
        }
        _buttonsVisibilityState.value = newParams != initialState
    }
}
