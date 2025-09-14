package ru.practicum.android.diploma.common.data

import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.data.mapper.IndustryMapper
import ru.practicum.android.diploma.common.data.model.IndustriesRequest
import ru.practicum.android.diploma.common.data.model.IndustriesResponse
import ru.practicum.android.diploma.common.data.model.NetworkClient
import ru.practicum.android.diploma.common.domain.IndustryRepository
import ru.practicum.android.diploma.common.domain.entity.Industry

class IndustryRepositoryImpl(
    private val networkClient: NetworkClient,
    private val mapper: IndustryMapper
) : IndustryRepository {

    companion object {
        private const val SUCCESS = 200
        private const val ERROR = 500
        private const val INTERNET_ERROR = -1
    }

    override suspend fun getIndustries(): Resource<List<Industry>> {
        val response = networkClient.doRequest(IndustriesRequest())

        return when (response.resultCode) {
            SUCCESS -> {
                val industryList = (response as IndustriesResponse).industriesDto.map { item ->
                    mapper.map(item)
                }
                Resource.Success(industryList)
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
