package ru.practicum.android.diploma.common.data.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.practicum.android.diploma.common.data.model.AreasRequest
import ru.practicum.android.diploma.common.data.model.FilteredVacancyRequest
import ru.practicum.android.diploma.common.data.model.IndustriesRequest
import ru.practicum.android.diploma.common.data.model.NetResponse
import ru.practicum.android.diploma.common.data.model.NetworkClient
import ru.practicum.android.diploma.common.data.model.VacanciesRequest
import ru.practicum.android.diploma.common.data.model.VacancyRequest

class RetrofitNetworkClient(private val headHunterApi: HeadHunterApi, private val token: String) : NetworkClient {

    companion object {
        private const val SUCCESS = 200
        private const val SERVER_ERROR = 500
        private const val ERROR = 400
    }

    override suspend fun doRequest(dto: Any): NetResponse {

        return withContext(Dispatchers.IO) {
            try {
                when (dto) {
                    is VacanciesRequest -> {
                        val response = headHunterApi.searchAllVacancies(token)
                        response.apply { resultCode = SUCCESS }
                    }

                    is VacancyRequest -> {
                        val response = headHunterApi.getVacancyById(
                            token,
                            dto.id
                        )
                        response.apply { resultCode = SUCCESS }
                    }

                    is AreasRequest -> {
                        val response = headHunterApi.getAreas()
                        Log.d("TAG", "doRequest: ${response.areaDto}")
                        response.apply { resultCode = SUCCESS }

                    }

                    is IndustriesRequest -> {
                        val response = headHunterApi.getIndustries()
                        response.apply { resultCode = SUCCESS }
                    }

                    is FilteredVacancyRequest -> {

                        val options: HashMap<String, String> = HashMap()

                        if (dto.areaId != null) {
                            options["area"] = dto.areaId.toString()
                        }
                        if (dto.industryId != null) {
                            options["industry"] = dto.industryId.toString()
                        }
                        if (dto.text != null) {
                            options["text"] = dto.text
                        }
                        if (dto.salary != null) {
                            options["salary"] = dto.salary.toString()
                        }
                        if (dto.page != null) {
                            options["page"] = dto.page.toString()
                        }
                        if (dto.onlyWithSalary != null) {
                            options["only_with_salary"] = dto.onlyWithSalary.toString()
                        }

                        val response = headHunterApi.searchVacanciesWithFilter(
                            token,
                            options
                        )
                        response.apply { resultCode = SUCCESS }
                    }

                    else -> {
                        NetResponse().apply { resultCode = SERVER_ERROR }
                    }
                }
            } catch (e: Throwable) {
                NetResponse().apply { resultCode = ERROR }
            }
        }
    }
}

