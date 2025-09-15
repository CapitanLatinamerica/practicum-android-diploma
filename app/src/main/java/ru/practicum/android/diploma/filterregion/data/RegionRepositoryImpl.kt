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
                val country = response.areaDto.find { it.id == countryId }

                if (country == null) {
                    Resource.Error("Страна не найдена")  // → showError
                } else {
                    val regions = country.areas?.map { mapper.mapAreaDtoToArea(it) } ?: emptyList()

                    if (regions.isEmpty()) {
                        Resource.Success(emptyList())  // → будет обработано как Empty в ViewModel
                    } else {
                        Resource.Success(regions)
                    }
                }
            }
            response is AreasResponse && response.resultCode == ERROR -> {
                Resource.Error("Ошибка API: код ${response.resultCode}")  // → showError
            }
            else -> {
                Resource.Error("Неверный формат ответа")  // → showError
            }
        }
    }

}
