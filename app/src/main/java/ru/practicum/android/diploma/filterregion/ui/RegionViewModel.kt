package ru.practicum.android.diploma.filterregion.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.domain.entity.Area
import ru.practicum.android.diploma.filterregion.domain.RegionInteractor

class RegionViewModel(
    private val interactor: RegionInteractor
) : ViewModel() {

    private val _regionState = MutableLiveData<RegionState>() // LiveData для внутреннего обновления состояния
    val regionState: LiveData<RegionState> = _regionState

    private var allRegions: List<Area> = emptyList()

    // Запрос списка регионов для указанного countryId
    fun getRegions(countryId: Int?) {
        viewModelScope.launch {
            _regionState.value = RegionState.Loading
            when(val result = interactor.getRegions(countryId)) {
                is Resource.Success -> {
                    val regions = result.data
                    if (regions != null && regions.isNotEmpty()) {
                        allRegions = regions
                        _regionState.value = RegionState.Content(regions)
                    } else {
                        _regionState.value = RegionState.Empty("Нет данных о регионах")
                    }
                }
                is Resource.Error -> {
                    _regionState.value = RegionState.Error(result.message ?: "Ошибка загрузки")
                }
            }
        }
    }

    // Фильтрация регионов по запросу
    fun filterRegions(query: String) {
        val filteredRegions = if (query.isEmpty()) {
            allRegions
        } else {
            allRegions.filter { region ->
                region.name.contains(query, ignoreCase = true)
            }
        }

        if (filteredRegions.isEmpty() && query.isNotEmpty()) {
            _regionState.value = RegionState.Empty("По запросу '$query' ничего не найдено")
        } else if (filteredRegions.isEmpty()) {
            _regionState.value = RegionState.Empty("Нет регионов")
        } else {
            _regionState.value = RegionState.Content(filteredRegions)
        }
    }
}
