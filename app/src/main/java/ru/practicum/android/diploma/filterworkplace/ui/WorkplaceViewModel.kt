package ru.practicum.android.diploma.filterworkplace.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.filtersettings.data.FilterParameters
import ru.practicum.android.diploma.filtersettings.domain.FilteringUseCase

class WorkplaceViewModel(
    private val filteringUseCase: FilteringUseCase
) : ViewModel() {
    private val _workplaceState = MutableLiveData(WorkplaceState())
    val workplaceState: LiveData<WorkplaceState> = _workplaceState

    // LiveData для отслеживания наличия выбранной страны
    private val _hasSelectedCountry = MutableLiveData<Boolean>()
    val hasSelectedCountry: LiveData<Boolean> = _hasSelectedCountry

    init {
        loadExistingFilterSettings()
    }

    private fun loadExistingFilterSettings() {
        CoroutineScope(Dispatchers.IO).launch {
            val existingParams = filteringUseCase.loadParameters()
            existingParams?.let { params ->
                // Восстанавливаем состояние из сохраненных параметров
                _workplaceState.postValue(
                    WorkplaceState(
                        country = params.workplace,
                        region = "" // Регион пока не используется в FilterParameters
                    )
                )
                _hasSelectedCountry.postValue(params.workplace.isNotEmpty())
            }
        }
    }

    fun onCountrySelected(value: String) {
        _workplaceState.value = _workplaceState.value?.copy(country = value)
        _hasSelectedCountry.value = value.isNotEmpty()

        // Сохраняем страну сразу при выборе
        saveWorkplaceToPreferences(value)
    }

    fun onRegionSelected(value: String) {
        _workplaceState.value = _workplaceState.value?.copy(region = value)
    }

    fun clearCountry() {
        _workplaceState.value = _workplaceState.value?.copy(country = "")
        _hasSelectedCountry.value = false
        // Очищаем workplace в настройках
        saveWorkplaceToPreferences("")
    }

    fun clearRegion() {
        _workplaceState.value = _workplaceState.value?.copy(region = "")
    }

    private fun saveWorkplaceToPreferences(workplace: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentParams = filteringUseCase.loadParameters() ?: FilterParameters()
            val updatedParams = currentParams.copy(workplace = workplace)
            filteringUseCase.saveParameters(updatedParams)
        }
    }

    // Метод для применения всех изменений (если нужно)
    fun applyChanges() {
        val currentState = _workplaceState.value ?: return
        saveWorkplaceToPreferences(currentState.country ?: "")
    }
}
