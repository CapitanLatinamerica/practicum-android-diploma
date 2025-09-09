package ru.practicum.android.diploma.common.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.practicum.android.diploma.Resource
import ru.practicum.android.diploma.common.data.domain.api.AreaDto
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
import ru.practicum.android.diploma.common.domain.entity.Industry
import ru.practicum.android.diploma.common.domain.entity.Vacancy
import ru.practicum.android.diploma.search.domain.model.VacanciesPage

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

    override fun searchAllVacancies(): List<Vacancy> {
        TODO("Not yet implemented")
    }

    override fun searchVacanciesWithFilter(filteredVacancyParameters: FilteredVacancyParameters): List<Vacancy> {
        TODO("Not yet implemented")
    }

    // Получение детали вакансии по ID — делает сетевой вызов через NetworkClient
    // преобразует полученный ответ в VacancyDto, мапит в Vacancy и возвращает Resource
    override suspend fun getVacancyDetailsById(id: String): Resource<Vacancy> {

        val response = networkClient.doRequest(VacancyRequest(id))

        when (response) {
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

                val vacancy = VacancyMapper.mapFromVacancyDtoToVacancy(vacancyDto)

                return Resource.Success(vacancy)
            }
            else -> {
                return Resource.Error("Unexpected response type")
            }
        }
    }

    override fun deleteById(id: String): Boolean {
        TODO("Not implemented deleteById")
    }

    override fun getIndustries(): List<Industry> {
        // Временно
        return emptyList()
    }

    override fun getAreas(): List<AreaDto> {
        // Временно
        return emptyList()
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
