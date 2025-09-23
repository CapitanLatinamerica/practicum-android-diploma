package ru.practicum.android.diploma.filterregion.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.domain.entity.Area
import ru.practicum.android.diploma.filterregion.domain.RegionInteractor
import ru.practicum.android.diploma.filtersettings.data.FilterParameters
import ru.practicum.android.diploma.filtersettings.domain.FilteringUseCase

class RegionViewModel(
    private val interactor: RegionInteractor,
    private val filteringUseCase: FilteringUseCase
) : ViewModel() {

    private val _regionState =
        MutableLiveData<RegionState>() // LiveData для внутреннего обновления состояния
    val regionState: LiveData<RegionState> = _regionState

    private var allRegions: List<Area> = emptyList()

    fun loadFilterParameters(selectedRegion: Area) {
        viewModelScope.launch {
            val currentParams = filteringUseCase.loadParameters() ?: FilterParameters()
            // Если у региона есть parentId, находим страну
            val countryName = if (selectedRegion.parentId != null) {
                when (val result = findCountryByRegion(selectedRegion.parentId)) {
                    is Resource.Success -> result.data?.name ?: currentParams.country
                    is Resource.Error -> currentParams.country
                }
            } else {
                currentParams.country
            }
            val updatedParams = currentParams.copy(
                country = countryName,
                countryId = selectedRegion.parentId ?: currentParams.countryId,
                region = selectedRegion.name,
                regionId = selectedRegion.id
            )
            filteringUseCase.saveParameters(updatedParams)
            _regionState.value = RegionState.RegionSelected
        }
    }

    fun loadRegionsFromUseCase() {
        viewModelScope.launch {
            val params = filteringUseCase.loadParameters()
            val countryId = params?.countryId ?: 0 // Если null, используем 0

            if (countryId == 0) {
                // Если countryId = 0, загружаем все регионы всех стран
                getRegions(null)
            } else {
                // Иначе загружаем регионы конкретной страны
                getRegions(countryId)
            }
        }
    }

    // Запрос списка регионов для указанного countryId
    fun getRegions(countryId: Int?) {
        viewModelScope.launch {
            _regionState.value = RegionState.Loading
            when (val result = interactor.getRegions(countryId)) {
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

    suspend fun findCountryByRegion(parentId: Int): Resource<Area?> {
        return interactor.findCountryByRegion(parentId)
    }
}
