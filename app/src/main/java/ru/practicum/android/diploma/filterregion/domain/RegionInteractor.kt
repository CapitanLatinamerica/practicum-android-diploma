package ru.practicum.android.diploma.filterregion.domain

import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.domain.entity.Area

interface RegionInteractor {
    suspend fun getRegions(countryId: Int?): Resource<List<Area>>
}
