package ru.practicum.android.diploma.filterregion.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.filterregion.domain.RegionInteractor

class RegionViewModel(
    private val interactor: RegionInteractor
) : ViewModel() {

    private val _regionState = MutableLiveData<RegionState>() // LiveData для внутреннего обновления состояния
    val regionState: LiveData<RegionState> = _regionState

    // Запрос списка регионов для указанного countryId
    fun getRegions(countryId: String) {
        viewModelScope.launch {
            _regionState.value = RegionState.Loading
            when(val result = interactor.getRegions(countryId)) {
                is Resource.Success -> {
                    Log.d("RegionViewModel", "Успех: ${result.data?.size ?: 0} регионов")
                    val regions = result.data
                    if (regions != null && regions.isNotEmpty()) {
                        _regionState.value = RegionState.Content(regions)
                    } else {
                        Log.w("RegionViewModel", "Регионы null или пустые")
                        _regionState.value = RegionState.Error("Нет данных о регионах")
                    }
                }
                is Resource.Error -> {
                    Log.e("RegionViewModel", "Ошибка: ${result.message}")
                    _regionState.value = RegionState.Error(result.message ?: "Ошибка загрузки")
                }
            }
        }
    }
}

