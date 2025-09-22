package ru.practicum.android.diploma.common.data

import ru.practicum.android.diploma.ErrorConst
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

    override suspend fun getIndustries(): Resource<List<Industry>> {
        val response = networkClient.doRequest(IndustriesRequest())

        return when (response.resultCode) {
            ErrorConst.SUCCESS -> {
                val industryList = (response as IndustriesResponse).industriesDto.map { item ->
                    mapper.map(item)
                }
                Resource.Success(industryList)
            }

            ErrorConst.SERVER_ERROR -> {
                Resource.Error("Ошибка сервера")
            }

            ErrorConst.INTERNET_ERROR -> {
                Resource.Error("Проверьте подключение к интернету")
            }

            else -> {
                Resource.Error("Ошибка: код ${response.resultCode}")
            }
        }
    }
}
