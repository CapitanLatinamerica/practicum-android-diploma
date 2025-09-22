package ru.practicum.android.diploma.common.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.practicum.android.diploma.ErrorConst
import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.data.db.AppDataBase
import ru.practicum.android.diploma.common.data.domain.api.VacancyDto
import ru.practicum.android.diploma.common.data.mapper.VacancyMapper
import ru.practicum.android.diploma.common.data.model.FilteredVacancyRequest
import ru.practicum.android.diploma.common.data.model.FilteredVacancyResponse
import ru.practicum.android.diploma.common.data.model.NetResponse
import ru.practicum.android.diploma.common.data.model.NetworkClient
import ru.practicum.android.diploma.common.data.model.VacancyRequest
import ru.practicum.android.diploma.common.data.model.VacancyResponse
import ru.practicum.android.diploma.common.domain.VacancyRepository
import ru.practicum.android.diploma.common.domain.entity.FilteredVacancyParameters
import ru.practicum.android.diploma.common.domain.entity.Vacancy
import ru.practicum.android.diploma.search.domain.model.VacanciesPage

class VacancyRepositoryImpl(
    private val networkClient: NetworkClient,
    private val mapper: VacancyMapper,
    private val dataBase: AppDataBase
) : VacancyRepository {

    override fun searchVacancies(filteredVacancyParameters: FilteredVacancyParameters): Flow<Resource<VacanciesPage>> =
        flow {
            val request =
                FilteredVacancyRequest(
                    areaId = filteredVacancyParameters.areaId,
                    industryId = filteredVacancyParameters.industryId,
                    text = if (filteredVacancyParameters.text.isNullOrBlank()) null else filteredVacancyParameters.text,
                    salary = filteredVacancyParameters.salary,
                    page = filteredVacancyParameters.page,
                    onlyWithSalary = filteredVacancyParameters.onlyWithSalary
                )

            val response = networkClient.doRequest(request)

            when (response.resultCode) {
                ErrorConst.INTERNET_ERROR, ErrorConst.TIMEOUT_ERROR -> {
                    emit(Resource.Error("Проверьте подключение к интернету"))
                }

                ErrorConst.SUCCESS -> {
                    doOnSuccess(response)
                }

                ErrorConst.SERVER_ERROR -> {
                    emit(Resource.Error("Ошибка сервера"))
                }

                else -> {
                    emit(Resource.Error("Ошибка: код ${response.resultCode}"))
                }
            }

        }
            .flowOn(Dispatchers.IO)

    override fun searchAllVacancies(): List<Vacancy> {
        TODO("Not yet implemented")
    }

    override fun searchVacanciesWithFilter(filteredVacancyParameters: FilteredVacancyParameters): List<Vacancy> {
        TODO("Not yet implemented")
    }

    // Получение детали вакансии по ID
    override suspend fun getVacancyDetailsById(id: String): Resource<Vacancy> {
        val response = networkClient.doRequest(VacancyRequest(id))

        return when (response.resultCode) {
            ErrorConst.SUCCESS -> {
                resource(response)
            }

            ErrorConst.INTERNET_ERROR -> {
                getVacancyFromDatabase(id)?.let { vacancy ->
                    Resource.Success(vacancy)
                } ?: Resource.Error("Нет интернета")
            }

            ErrorConst.SERVER_ERROR -> {
                Resource.Error("Ошибка сервера")
            }

            ErrorConst.NOT_FOUND -> {
                Resource.Error("404")
            }

            else -> {
                Resource.Error("Ошибка: код ${response.resultCode}")
            }
        }
    }

    private fun resource(response: NetResponse): Resource<Vacancy> {
        return when (response) {
            is VacancyResponse -> {
                val vacancyDto = VacancyDto(
                    addressDto = response.addressDto,
                    areaDto = response.areaDto,
                    contactsDto = response.contactsDto,
                    description = response.description,
                    employerDto = response.employerDto,
                    employmentDto = response.employmentDto,
                    experienceDto = response.experienceDto,
                    id = response.id,
                    industryDto = response.industryDto,
                    name = response.name,
                    salaryDto = response.salaryDto,
                    scheduleDto = response.scheduleDto,
                    skills = response.skills,
                    url = response.url
                )

                val vacancy = mapper.mapFromVacancyDtoToVacancy(vacancyDto)

                Resource.Success(vacancy)
            }

            else -> {
                Resource.Error("Unexpected response type")
            }
        }
    }

    private suspend fun getVacancyFromDatabase(id: String): Vacancy? {
        val entity = dataBase.vacancyDao().getVacancyById(id)
        return entity?.let {
            mapper.mapFromEntityToVacancy(entity)
        }
    }

    override fun deleteById(id: String): Boolean {
        TODO("Not implemented deleteById")
    }

    override fun deleteVacancyFromFavorites(id: String) {
        TODO("Not implemented deleteVacancyFromFavorites")
    }

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
