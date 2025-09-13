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

    private var initialState: FilterState = FilterState()

    private var initialized = false

    val buttonsVisibilityState = MutableLiveData(false)

    init {
        viewModelScope.launch {
            val savedParams = filteringUseCase.loadParameters()
            val starting = if (savedParams != null) mapper.mapParamsToUi(savedParams) else FilterState()
            initialState = starting
            _filterState.postValue(starting)
            buttonsVisibilityState.postValue(starting != initialState)
            initialized = true
        }
    }

    fun onWorkplaceSelected(value: String) {
        val newParam = _filterState.value?.copy(workplace = value) ?: FilterState(workplace = value)
        _filterState.value = newParam
        saveFilteringParam(newParam)
    }

    fun onIndustrySelected(value: String) {
        val newParam = _filterState.value?.copy(industry = value) ?: FilterState(industry = value)
        _filterState.value = newParam
        saveFilteringParam(newParam)
    }

    fun onSalaryTextChanged(text: String) {
        val newParam = _filterState.value?.copy(salary = text) ?: FilterState(salary = text)
        _filterState.value = newParam
        saveFilteringParam(newParam)
    }

    fun onOnlyWithSalaryToggled(isChecked: Boolean) {
        val newParam = _filterState.value?.copy(onlyWithSalary = isChecked) ?: FilterState(onlyWithSalary = isChecked)
        _filterState.value = newParam
        saveFilteringParam(newParam)
    }

    fun clearWorkplace() {
        val newParam = _filterState.value?.copy(workplace = "") ?: FilterState(workplace = "")
        _filterState.value = newParam
        saveFilteringParam(newParam)
    }

    fun clearIndustry() {
        val newParam = _filterState.value?.copy(industry = "") ?: FilterState(industry = "")
        _filterState.value = newParam
        saveFilteringParam(newParam)
    }

    private fun saveFilteringParam(state: FilterState) {
        if (!initialized) return
        buttonsVisibilityState.postValue(state != initialState)
        viewModelScope.launch {
            filteringUseCase.saveParameters(mapper.mapParamsToDomain(state))
        }
    }

}
