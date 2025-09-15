package ru.practicum.android.diploma.filterregion.data

import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.domain.entity.Area
import ru.practicum.android.diploma.filterregion.domain.RegionInteractor
import ru.practicum.android.diploma.filterregion.domain.RegionRepository

class RegionInteractorImpl(
    private val repository: RegionRepository
) : RegionInteractor {
    override suspend fun getRegions(countryId: Int?): Resource<List<Area>> {
        return repository.getRegions(countryId)
    }

    override suspend fun findCountryByRegion(parentId: Int): Resource<Area?> {
        return repository.findCountryByRegion(parentId)
    }
}
