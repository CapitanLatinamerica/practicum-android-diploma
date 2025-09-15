package ru.practicum.android.diploma.filterregion.domain

import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.domain.entity.Area

interface RegionRepository {
    suspend fun getRegions(countryId: String): Resource<List<Area>>
}
