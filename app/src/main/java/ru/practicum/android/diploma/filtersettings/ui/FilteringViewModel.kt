package ru.practicum.android.diploma.filtersettings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class FilteringViewModel : ViewModel() {

    private val _filterState = MutableLiveData(FilterState())
    val filterState: LiveData<FilterState> = _filterState

    private var initialState: FilterState = FilterState()

    val buttonsVisibilityState: LiveData<Boolean> = _filterState.map { current ->
        current != initialState
    }

    fun onSalaryTextChanged(text: String) {
        _filterState.value = _filterState.value?.copy(salary = text)
    }

    fun onOnlyWithSalaryToggled(isChecked: Boolean) {
        _filterState.value = _filterState.value?.copy(onlyWithSalary = isChecked)
    }

    fun onWorkplaceSelected(value: String) {
        _filterState.value = _filterState.value?.copy(workplace = value)
    }

    fun onIndustrySelected(value: String) {
        _filterState.value = _filterState.value?.copy(industry = value)
    }

    fun clearWorkplace() {
        _filterState.value = _filterState.value?.copy(workplace = "")
    }

    fun clearIndustry() {
        _filterState.value = _filterState.value?.copy(industry = "")
    }

}
