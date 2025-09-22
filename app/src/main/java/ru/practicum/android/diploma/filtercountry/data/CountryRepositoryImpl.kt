package ru.practicum.android.diploma.filtercountry.data

import ru.practicum.android.diploma.ErrorConst
import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.data.mapper.AreaMapper
import ru.practicum.android.diploma.common.data.model.AreasRequest
import ru.practicum.android.diploma.common.data.model.AreasResponse
import ru.practicum.android.diploma.common.data.model.NetworkClient
import ru.practicum.android.diploma.common.domain.entity.Area
import ru.practicum.android.diploma.filtercountry.domain.CountryRepository

class CountryRepositoryImpl(
    private val networkClient: NetworkClient,
    private val mapper: AreaMapper
) : CountryRepository {

    override suspend fun getCountries(): Resource<List<Area>> {
        val response = networkClient.doRequest(AreasRequest())

        return when {
            response is AreasResponse && response.resultCode == ErrorConst.SUCCESS -> {
                val countries = response.areaDto
                    .filter { it.parentId.isNullOrEmpty() }
                    .map { mapper.mapAreaDtoToArea(it) }

                if (countries.isEmpty()) {
                    Resource.Error("Список стран пуст")
                } else {
                    Resource.Success(countries)
                }
            }

            response is AreasResponse && response.resultCode == ErrorConst.SERVER_ERROR -> {
                Resource.Error("Ошибка API: код ${response.resultCode}")
            }

            else -> {
                Resource.Error("Неверный формат ответа")
            }
        }
    }
}
