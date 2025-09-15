package ru.practicum.android.diploma.filterregion.data

import android.util.Log
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
        Log.d("RegionRepository", "Поиск регионов для countryId: $countryId")

        val response = networkClient.doRequest(AreasRequest())

        return when {
            response is AreasResponse && response.resultCode == SUCCESS -> {
                // 1. Находим страну по ID
                val country = response.areaDto.find { it.id == countryId }

                if (country == null) {
                    Log.w("RegionRepository", "Страна с ID '$countryId' не найдена")
                    Resource.Error("Страна не найдена")
                } else {
                    // 2. Берем вложенные регионы из поля areas
                    val regions = country.areas?.map { mapper.mapAreaDtoToArea(it) } ?: emptyList()

                    Log.d("RegionRepository", "Найдено регионов: ${regions.size} для страны '${country.name}'")

                    if (regions.isEmpty()) {
                        Resource.Error("Список регионов пуст")
                    } else {
                        Resource.Success(regions)
                    }
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
