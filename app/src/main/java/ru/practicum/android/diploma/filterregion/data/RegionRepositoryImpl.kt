package ru.practicum.android.diploma.filterregion.data

import android.util.Log
import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.data.domain.api.AreaDto
import ru.practicum.android.diploma.common.data.mapper.AreaMapper
import ru.practicum.android.diploma.common.data.model.AreasRequest
import ru.practicum.android.diploma.common.data.model.AreasResponse
import ru.practicum.android.diploma.common.data.model.NetworkClient
import ru.practicum.android.diploma.common.domain.entity.Area
import ru.practicum.android.diploma.filterregion.domain.RegionRepository

class RegionRepositoryImpl(
    private val networkClient: NetworkClient,
    private val mapper: AreaMapper
) : RegionRepository {

    companion object {
        private const val SUCCESS = 200
        private const val ERROR = 500
    }

    override suspend fun getRegions(countryId: Int?): Resource<List<Area>> {

        val response = networkClient.doRequest(AreasRequest())

        return when {
            response is AreasResponse && response.resultCode == SUCCESS -> {
                if (countryId == null) {
                    getAllRegionsFromAllCountries(response.areaDto)
                } else {
                    // Преобразуем Int в String для сравнения
                    getRegionsForSpecificCountry(response.areaDto, countryId.toString())
                }
            }

            response is AreasResponse && response.resultCode == ERROR -> {
                Resource.Error("Ошибка API: код ${response.resultCode}")
            }

            else -> {
                Resource.Error("Неверный формат ответа")
            }
        }
    }

    private fun getAllRegionsFromAllCountries(allAreas: List<AreaDto>): Resource<List<Area>> {


        val allRegions = allAreas.flatMap { country ->
            country.areas?.map { mapper.mapAreaDtoToArea(it) } ?: emptyList()
        }

        val filteredRegions = allRegions
            .filter {
                it.parentId != null
            }
            .sortedBy { it.name }

        return Resource.Success(filteredRegions)
    }

    private fun getRegionsForSpecificCountry(allAreas: List<AreaDto>, countryId: String): Resource<List<Area>> {

        val country = allAreas.find { it.id == countryId }
        return if (country == null) {
            Resource.Error("Страна не найдена")
        } else {
            val regions = country.areas?.map { mapper.mapAreaDtoToArea(it) } ?: emptyList()
            Resource.Success(regions)
        }
    }

}
