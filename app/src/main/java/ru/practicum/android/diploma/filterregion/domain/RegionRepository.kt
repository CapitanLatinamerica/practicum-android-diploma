package ru.practicum.android.diploma.filterregion.domain

import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.domain.entity.Area

interface RegionRepository {
    suspend fun getRegions(countryId: Int?): Resource<List<Area>>
    suspend fun findCountryByRegion(regionId: Int): Resource<Area?>
}
