package ru.practicum.android.diploma.filtercountry.data

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
    companion object {
        private const val SUCCESS = 200
        private const val ERROR = 500
    }

    override suspend fun getCountries(): Resource<List<Area>> {
        return try {
            val response = networkClient.doRequest(AreasRequest())

            when {
                response is AreasResponse && response.resultCode == SUCCESS -> {
                    val countries = response.areaDto
                        .filter { it.parentId.isNullOrEmpty() }
                        .map { mapper.mapAreaDtoToArea(it) }

                    if (countries.isEmpty()) {
                        Resource.Error("Список стран пуст")
                    } else {
                        Resource.Success(countries)
                    }
                }

                response is AreasResponse && response.resultCode == ERROR -> {
                    Resource.Error("Ошибка API: код ${response.resultCode}")
                }

                else -> {
                    Resource.Error("Неверный формат ответа")
                }
            }
        } catch (e: Exception) {
            Resource.Error("Исключение: ${e.message}")
        }
    }
}
