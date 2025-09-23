package ru.practicum.android.diploma.common.data.network

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.practicum.android.diploma.ErrorConst
import ru.practicum.android.diploma.Tools
import ru.practicum.android.diploma.common.data.model.AreasRequest
import ru.practicum.android.diploma.common.data.model.AreasResponse
import ru.practicum.android.diploma.common.data.model.FilteredVacancyRequest
import ru.practicum.android.diploma.common.data.model.IndustriesRequest
import ru.practicum.android.diploma.common.data.model.IndustriesResponse
import ru.practicum.android.diploma.common.data.model.NetResponse
import ru.practicum.android.diploma.common.data.model.VacanciesRequest
import ru.practicum.android.diploma.common.data.model.VacancyRequest
import java.net.ConnectException
import java.net.SocketTimeoutException

class RetrofitNetworkClient(
    private val headHunterApi: HeadHunterApi,
    private val token: String,
    private val context: Context
) : NetworkClient {

    companion object {
        private const val TAG = "RetrofitNetworkClient"
    }

    override suspend fun doRequest(dto: Any): NetResponse {
        if (!Tools.isConnected(context)) return NetResponse().internetError()

        return withContext(Dispatchers.IO) {
            try {
                when (dto) {
                    is VacanciesRequest ->
                        headHunterApi.searchAllVacancies(token)
                            .success()

                    is VacancyRequest ->
                        headHunterApi.getVacancyById(
                            token,
                            dto.id
                        ).success()

                    is AreasRequest ->
                        AreasResponse(
                            headHunterApi.getAreas(token)
                        ).success()

                    is IndustriesRequest ->
                        IndustriesResponse(
                            headHunterApi.getIndustries(token)
                        ).success()

                    is FilteredVacancyRequest ->
                        handleFilteredRequest(dto)

                    else ->
                        NetResponse().serverError()
                }
            } catch (e: SocketTimeoutException) {
                Log.e(TAG, "Socket timeout", e)
                NetResponse().error(ErrorConst.TIMEOUT_ERROR)
            } catch (e: retrofit2.HttpException) {
                Log.e(TAG, "HTTP ${e.code()} ${e.message()}", e)
                NetResponse().error(e.code())
            } catch (e: ConnectException) {
                Log.e(TAG, "Network error", e)
                NetResponse().error(ErrorConst.INTERNET_ERROR)
            }
        }
    }

    private suspend fun handleFilteredRequest(
        dto: FilteredVacancyRequest
    ): NetResponse {
        val options = buildMap {
            with(dto) {
                areaId?.let {
                    put("area", it.toString())
                }
                industryId?.let {
                    put("industry", it.toString())
                }
                text?.let { put("text", it) }
                salary?.let {
                    put("salary", it.toString())
                }
                page?.let {
                    put("page", it.toString())
                }
                onlyWithSalary?.let {
                    put(
                        "only_with_salary",
                        it.toString()
                    )
                }
            }
        }

        return headHunterApi
            .searchVacanciesWithFilter(
                token,
                options
            )
            .success()
    }

    private fun NetResponse.success(): NetResponse = apply { resultCode = ErrorConst.SUCCESS }
    private fun NetResponse.error(code: Int = ErrorConst.ERROR): NetResponse = apply { resultCode = code }
    private fun NetResponse.serverError(): NetResponse = apply { resultCode = ErrorConst.SERVER_ERROR }
    private fun NetResponse.internetError(): NetResponse = apply { resultCode = ErrorConst.INTERNET_ERROR }
}
