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
        private const val INTERNET_ERROR = -1
    }

    override suspend fun getCountries(): Resource<List<Area>> {
        val response = networkClient.doRequest(AreasRequest()) as AreasResponse

        return when (response.resultCode) {
            SUCCESS -> {
                val countriesList = response.areaDto.map { item ->
                    mapper.mapAreaDtoToArea(item)
                }
                Resource.Success(countriesList)
            }

            ERROR -> {
                Resource.Error("Ошибка сервера")
            }

            INTERNET_ERROR -> {
                Resource.Error("Проверьте подключение к интернету")
            }

            else -> {
                Resource.Error("Ошибка: код ${response.resultCode}")
            }
        }
    }
}
