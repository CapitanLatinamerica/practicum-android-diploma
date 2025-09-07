package ru.practicum.android.diploma.search.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.data.mapper.VacancyMapper
import ru.practicum.android.diploma.common.data.model.FilteredVacancyRequest
import ru.practicum.android.diploma.common.data.model.FilteredVacancyResponse
import ru.practicum.android.diploma.common.data.model.NetResponse
import ru.practicum.android.diploma.common.data.model.NetworkClient
import ru.practicum.android.diploma.search.domain.model.VacanciesPage
import ru.practicum.android.diploma.search.domain.repository.VacancyRepository

class VacancyRepositoryImpl(private val networkClient: NetworkClient) : VacancyRepository {

    companion object {
        private const val SUCCESS = 200
        private const val ERROR = 500
    }

    override fun searchVacancies(query: String, page: Int): Flow<Resource<VacanciesPage>> =
        flow {

            val request =
                FilteredVacancyRequest(
                    areaId = null,
                    industryId = null,
                    text = if (query.isBlank()) null else query,
                    salary = null,
                    page = page,
                    onlyWithSalary = null,
                )

            val response = networkClient.doRequest(request)

            when (response.resultCode) {
                -1 -> {
                    emit(Resource.Error("Проверьте подключение к интернету"))
                }

                SUCCESS -> {
                    doOnSuccess(response)
                }

                ERROR -> {
                    emit(Resource.Error("Ошибка сервера"))
                }

                else -> {
                    emit(Resource.Error("Ошибка: код ${response.resultCode}"))
                }
            }

        }
            .flowOn(Dispatchers.IO)

    private suspend fun FlowCollector<Resource<VacanciesPage>>.doOnSuccess(
        response: NetResponse
    ) {
        when (response) {
            is FilteredVacancyResponse -> {
                val domainList =
                    response.items.map {
                        VacancyMapper.mapFromVacancyDtoToVacancy(it)
                    }
                val pageObj =
                    VacanciesPage(
                        found = response.found,
                        items = domainList,
                        page = response.page,
                        pages = response.pages,
                    )
                emit(Resource.Success(pageObj))
            }
        }
    }
}
