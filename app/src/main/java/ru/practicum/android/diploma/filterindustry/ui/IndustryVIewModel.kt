package ru.practicum.android.diploma.filterindustry.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.common.data.model.GetIndustriesUseCase

class IndustryVIewModel(private val useCase: GetIndustriesUseCase) : ViewModel() {

    private val _industryState = MutableLiveData<IndustryState>()
    val industryState: LiveData<IndustryState> get() = _industryState

    fun getIndustries(selectedIndustryId: String? = null) {
        viewModelScope.launch {
            val resource = useCase.execute()
            if (resource.data != null) {
                _industryState.value = IndustryState.Content(
                    industryList = resource.data,
                    selectedIndustryId = selectedIndustryId
                )
            } else {
                _industryState.value = IndustryState.Error
            }
        }
    }
}
