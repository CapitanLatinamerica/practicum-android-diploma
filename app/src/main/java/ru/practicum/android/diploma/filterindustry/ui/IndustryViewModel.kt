package ru.practicum.android.diploma.filterindustry.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.common.domain.entity.Industry
import ru.practicum.android.diploma.filterindustry.domain.GetIndustriesUseCase
import ru.practicum.android.diploma.filtersettings.data.FilterParameters
import ru.practicum.android.diploma.filtersettings.domain.FilteringUseCase

class IndustryViewModel(
    private val getIndustriesUseCase: GetIndustriesUseCase,
    private val filteringUseCase: FilteringUseCase
) : ViewModel() {

    private val _industryState = MutableLiveData<IndustryState>()
    val industryState: LiveData<IndustryState> get() = _industryState

    fun loadInitialIndustries() {
        viewModelScope.launch {
            _industryState.value = IndustryState.Loading
            // Загружаем параметры (suspend внутри useCase)
            val params = filteringUseCase.loadParameters()
            val selectedId = params?.industryId?.toString() ?: ""
            // Загружаем список отраслей и передаём selectedId в состояние
            val resource = getIndustriesUseCase.execute()
            if (resource.data != null) {
                _industryState.value = IndustryState.Content(
                    industryList = resource.data,
                    selectedIndustryId = selectedId
                )
            } else {
                _industryState.value = IndustryState.Error
            }
        }
    }

    fun saveSelectedIndustry(selectedIndustry: Industry?) {
        viewModelScope.launch {
            _industryState.value = IndustryState.Saving
            val currentParams = filteringUseCase.loadParameters()
            val updatedParams = (currentParams ?: FilterParameters()).copy(
                industry = selectedIndustry?.name ?: "",
                industryId = selectedIndustry?.id ?: 0
            )
            filteringUseCase.saveParameters(updatedParams)
            _industryState.value = IndustryState.Saved
        }
    }

}
