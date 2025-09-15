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

    override suspend fun getRegions(countryName: String): Resource<List<Area>> {
        Log.d("RegionRepository", "Поиск регионов для страны: $countryName")

        // Сначала получаем ВСЕ области чтобы найти ID страны
        val allAreasResponse = networkClient.doRequest(AreasRequest())

        return when {
            allAreasResponse is AreasResponse && allAreasResponse.resultCode == SUCCESS -> {
                // 2. Находим страну по имени
                val country = allAreasResponse.areaDto.find { it.name == countryName }

                if (country == null) {
                    Log.w("RegionRepository", "Страна '$countryName' не найдена")
                    Resource.Error("Страна не найдена")
                } else {
                    Log.d("RegionRepository", "Найдена страна: ${country.name} (ID: ${country.id})")

                    // 3. Теперь ищем регионы для этой страны
                    val regionsResponse = networkClient.doRequest(AreasRequest(parentId = country.id))

                    when {
                        regionsResponse is AreasResponse && regionsResponse.resultCode == SUCCESS -> {
                            val regions = regionsResponse.areaDto
                                .map { mapper.mapAreaDtoToArea(it) }

                            Log.d("RegionRepository", "Найдено регионов: ${regions.size}")

                            if (regions.isEmpty()) {
                                Resource.Error("Список регионов пуст")
                            } else {
                                Resource.Success(regions)
                            }
                        }
                        else -> {
                            Resource.Error("Ошибка при загрузке регионов")
                        }
                    }
                }
            }
            else -> {
                Resource.Error("Ошибка при загрузке списка областей")
            }
        }
    }

}
