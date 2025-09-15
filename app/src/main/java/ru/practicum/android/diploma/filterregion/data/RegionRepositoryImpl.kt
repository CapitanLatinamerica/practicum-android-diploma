package ru.practicum.android.diploma.filterregion.data

import ru.practicum.android.diploma.Resource
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

    override suspend fun getRegions(countryId: String): Resource<List<Area>> {
        val response = networkClient.doRequest(
            AreasRequest(parentId = countryId) // Теперь этот параметр определён
        )
        return when {
            response is AreasResponse && response.resultCode == SUCCESS -> {
                val regions = response.areaDto
                    .filter { it.parentId == countryId } // Фильтрация местоположения по стране
                    .map { mapper.mapAreaDtoToArea(it) }
                if (regions.isEmpty()) {
                    Resource.Error("Список регионов пуст")
                } else {
                    Resource.Success(regions)
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

}
