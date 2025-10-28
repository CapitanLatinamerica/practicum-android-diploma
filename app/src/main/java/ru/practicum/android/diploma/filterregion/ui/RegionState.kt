package ru.practicum.android.diploma.filterregion.ui

import ru.practicum.android.diploma.common.domain.entity.Area

sealed interface RegionState {
    object Loading : RegionState
    data class Content(val regions: List<Area>) : RegionState
    data class Empty(val message: String) : RegionState // Нет результатов
    data class Error(val message: String) : RegionState // Ошибка загрузки
    object RegionSelected : RegionState
}
