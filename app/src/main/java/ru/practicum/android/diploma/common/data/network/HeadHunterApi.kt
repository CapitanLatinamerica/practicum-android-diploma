package ru.practicum.android.diploma.common.data.network

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.QueryMap
import ru.practicum.android.diploma.common.data.domain.api.AreaDto
import ru.practicum.android.diploma.common.data.domain.api.IndustryDto
import ru.practicum.android.diploma.common.data.model.FilteredVacancyResponse
import ru.practicum.android.diploma.common.data.model.VacanciesResponse
import ru.practicum.android.diploma.common.data.model.VacancyResponse

interface HeadHunterApi {

    @GET("vacancies")
    suspend fun searchAllVacancies(
        @Header("Authorization") token: String,
    ): VacanciesResponse

    @GET("vacancies/{id}")
    suspend fun getVacancyById(
        @Header("Authorization") token: String,
        @Path("id") vacancyId: String
    ): VacancyResponse

    @GET("industries")
    suspend fun getIndustries(
        @Header("Authorization") token: String
    ): List<IndustryDto>

    @GET("areas")
    suspend fun getAreas(
        @Header("Authorization") token: String
    ): List<AreaDto>

    @GET("vacancies")
    suspend fun searchVacanciesWithFilter(
        @Header("Authorization") token: String,
        @QueryMap options: Map<String, String>
    ): FilteredVacancyResponse

}
