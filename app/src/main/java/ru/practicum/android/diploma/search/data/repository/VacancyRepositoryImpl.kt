package ru.practicum.android.diploma.search.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.data.mapper.VacancyMapper
import ru.practicum.android.diploma.common.data.model.FilteredVacancyRequest
import ru.practicum.android.diploma.common.data.model.FilteredVacancyResponse
import ru.practicum.android.diploma.common.data.model.NetworkClient
import ru.practicum.android.diploma.common.data.model.VacanciesResponse
import ru.practicum.android.diploma.search.domain.model.VacanciesPage
import ru.practicum.android.diploma.search.domain.repository.VacancyRepository

class VacancyRepositoryImpl(private val networkClient: NetworkClient) : VacancyRepository {

    private val TAG = "VacancyRepository"

    override fun searchVacancies(query: String, page: Int): Flow<Resource<VacanciesPage>> =
        flow {
                try {
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
                    Log.d(
                        TAG,
                        "response class=${response::class.java.name}, code=${response.resultCode}",
                    )
                    when (response.resultCode) {
                        -1 -> {
                            emit(Resource.Error("Проверьте подключение к интернету"))
                        }

                        200 -> {
                            when (response) {
                                is VacanciesResponse -> {
                                    val domainList =
                                        response.items.map { dto ->
                                            VacancyMapper.mapFromVacancyDtoToVacancy(dto)
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

                                else -> {
                                    Log.w(
                                        TAG,
                                        "Unexpected response type: ${response::class.java.name}",
                                    )
                                    emit(Resource.Error("Непредвиденный формат ответа от сервера"))
                                }
                            }
                        }

                        500 -> {
                            emit(Resource.Error("Ошибка сервера"))
                        }

                        else -> {
                            emit(Resource.Error("Ошибка: код ${response.resultCode}"))
                        }
                    }
                } catch (e: IOException) {
                    emit(Resource.Error("Проверьте подключение к интернету"))
                } catch (e: Exception) {
                    emit(Resource.Error("Ошибка: ${e.message ?: "Неизвестная ошибка"}"))
                }
            }
            .flowOn(Dispatchers.IO)
}
